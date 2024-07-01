package com.pvs.flutter_utils

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.pvs.flutter_utils.ads.NativeTemplateStyle
import com.pvs.flutter_utils.ads.NativeVideoViewFactory
import com.pvs.flutter_utils.ads.TemplateView
import com.pvs.flutter_utils.ads.NativeAdFactorySmall
import com.pvs.flutter_utils.ads.NativeAdFactoryMedium
import com.pvs.flutter_utils.home_widget.HomeWidgetLaunchIntent
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.googlemobileads.GoogleMobileAdsPlugin

/**
 * FlutterNativeAdPlugin
 */
class FlutterUtilsPlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    EventChannel.StreamHandler, PluginRegistry.NewIntentListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private val nativeChannel: MethodChannel? = null
    private var activity: Activity? = null
    private var flutterEngine: FlutterEngine? = null
    private lateinit var eventChannel: EventChannel
    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    private val template: TemplateView? = null
    var pluginBinding: FlutterPlugin.FlutterPluginBinding? = null
    private var receiver: BroadcastReceiver? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_utils")
        channel.setMethodCallHandler(this)

        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "home_widget/updates")
        eventChannel.setStreamHandler(this)

        context = flutterPluginBinding.getApplicationContext()
        flutterEngine = flutterPluginBinding.getFlutterEngine()
        pluginBinding = flutterPluginBinding
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getNativeAds" -> {
                GoogleMobileAdsPlugin.registerNativeAdFactory(
                    flutterEngine,
                    "listTile",
                    NativeAdFactorySmall(context)
                )
                GoogleMobileAdsPlugin.registerNativeAdFactory(
                    flutterEngine,
                    "listTileMedium",
                    NativeAdFactoryMedium(context)
                )

                val arguments: Map<String, String>? = call.arguments()
                val nativeId = arguments!!["nativeVideoID"]

                pluginBinding?.getPlatformViewRegistry()
                    ?.registerViewFactory("<platform-view-type>", NativeVideoViewFactory(nativeId))
            }

            "saveWidgetData" -> {
                if (call.hasArgument("id") && call.hasArgument("data")) {
                    val id = call.argument<String>("id")
                    val data = call.argument<Any>("data")
                    val prefs =
                        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit()
                    if (data != null) {
                        when (data) {
                            is Boolean -> prefs.putBoolean(id, data)
                            is Float -> prefs.putFloat(id, data)
                            is String -> prefs.putString(id, data)
                            is Double -> prefs.putLong(
                                id,
                                java.lang.Double.doubleToRawLongBits(data)
                            )

                            is Int -> prefs.putInt(id, data)
                            else -> result.error(
                                "-10",
                                "Invalid Type ${data!!::class.java.simpleName}. Supported types are Boolean, Float, String, Double, Long",
                                IllegalArgumentException()
                            )
                        }
                    } else {
                        prefs.remove(id)
                    }
                    result.success(prefs.commit())
                } else {
                    result.error(
                        "-1",
                        "InvalidArguments saveWidgetData must be called with id and data",
                        IllegalArgumentException()
                    )
                }
            }

            "getWidgetData" -> {
                if (call.hasArgument("id")) {
                    val id = call.argument<String>("id")
                    val defaultValue = call.argument<Any>("defaultValue")

                    val prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

                    val value = prefs.all[id] ?: defaultValue

                    if (value is Long) {
                        result.success(java.lang.Double.longBitsToDouble(value))
                    } else {
                        result.success(value)
                    }
                } else {
                    result.error(
                        "-2",
                        "InvalidArguments getWidgetData must be called with id",
                        IllegalArgumentException()
                    )
                }
            }

            "updateWidget" -> {
                val qualifiedName = call.argument<String>("qualifiedAndroidName")
                val className = call.argument<String>("android") ?: call.argument<String>("name")
                try {
                    val javaClass =
                        Class.forName(qualifiedName ?: "${context.packageName}.${className}")
                    val intent = Intent(context, javaClass)
                    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    val ids: IntArray =
                        AppWidgetManager.getInstance(context.applicationContext).getAppWidgetIds(
                            ComponentName(context, javaClass)
                        )
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    context.sendBroadcast(intent)
                    result.success(true)
                } catch (classException: ClassNotFoundException) {
                    result.error(
                        "-3",
                        "No Widget found with Name $className. Argument 'name' must be the same as your AppWidgetProvider you wish to update",
                        classException
                    )
                }
            }

            "setAppGroupId" -> {
                result.success(true)
            }

            "initiallyLaunchedFromHomeWidget" -> {
                return if (activity?.intent?.action?.equals(HomeWidgetLaunchIntent.HOME_WIDGET_LAUNCH_ACTION) == true) {
                    result.success(activity?.intent?.data?.toString() ?: "")
                } else {
                    result.success(null)
                }
            }

            "registerBackgroundCallback" -> {
                val dispatcher = ((call.arguments as Iterable<*>).toList()[0] as Number).toLong()
                val callback = ((call.arguments as Iterable<*>).toList()[1] as Number).toLong()
                saveCallbackHandle(context, dispatcher, callback)
                return result.success(true)
            }

            "isRequestPinWidgetSupported" -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    return result.success(false)
                }

                val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
                return result.success(appWidgetManager.isRequestPinAppWidgetSupported)
            }

            "requestPinWidget" -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    return result.success(null)
                }

                val qualifiedName = call.argument<String>("qualifiedAndroidName")
                val className = call.argument<String>("android") ?: call.argument<String>("name")

                try {
                    val javaClass =
                        Class.forName(qualifiedName ?: "${context.packageName}.${className}")
                    val myProvider = ComponentName(context, javaClass)

                    val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)

                    if (appWidgetManager.isRequestPinAppWidgetSupported) {
                        appWidgetManager.requestPinAppWidget(myProvider, null, null)
                    }

                    return result.success(null)
                } catch (classException: ClassNotFoundException) {
                    result.error(
                        "-4",
                        "No Widget found with Name $className. Argument 'name' must be the same as your AppWidgetProvider you wish to update",
                        classException
                    )
                }
            }

            "getInstalledWidgets" -> {
                try {
                    val pinnedWidgetInfoList = getInstalledWidgets(context)
                    result.success(pinnedWidgetInfoList)
                } catch (e: Exception) {
                    result.error("-5", "Failed to get installed widgets: ${e.message}", null)
                }
            }

            else -> {
                result.notImplemented()
            }
        }
    }

    private fun getInstalledWidgets(context: Context): List<Map<String, Any>> {
        val pinnedWidgetInfoList = mutableListOf<Map<String, Any>>()
        val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
        val installedProviders = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appWidgetManager.getInstalledProvidersForPackage(context.packageName, null)
        } else {
            appWidgetManager.installedProviders.filter { it.provider.packageName == context.packageName }
        }
        for (provider in installedProviders) {
            val widgetIds = appWidgetManager.getAppWidgetIds(provider.provider)
            for (widgetId in widgetIds) {
                val widgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
                pinnedWidgetInfoList.add(widgetInfoToMap(widgetId, widgetInfo))
            }
        }
        return pinnedWidgetInfoList
    }

    private fun widgetInfoToMap(
        widgetId: Int,
        widgetInfo: AppWidgetProviderInfo
    ): Map<String, Any> {
        val label = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            widgetInfo.loadLabel(context.packageManager).toString()
        } else {
            @Suppress("DEPRECATION")
            widgetInfo.label
        }

        return mapOf(
            WIDGET_INFO_KEY_WIDGET_ID to widgetId,
            WIDGET_INFO_KEY_ANDROID_CLASS_NAME to widgetInfo.provider.shortClassName,
            WIDGET_INFO_KEY_LABEL to label
        )
    }

    companion object {
        internal const val PREFERENCES = "HomeWidgetPreferences"
        internal const val TAG = "Native->"

        private const val INTERNAL_PREFERENCES = "InternalHomeWidgetPreferences"
        private const val CALLBACK_DISPATCHER_HANDLE = "callbackDispatcherHandle"
        private const val CALLBACK_HANDLE = "callbackHandle"

        private const val WIDGET_INFO_KEY_WIDGET_ID = "widgetId"
        private const val WIDGET_INFO_KEY_ANDROID_CLASS_NAME = "androidClassName"
        private const val WIDGET_INFO_KEY_LABEL = "label"

        private fun saveCallbackHandle(context: Context, dispatcher: Long, handle: Long) {
            context.getSharedPreferences(INTERNAL_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putLong(CALLBACK_DISPATCHER_HANDLE, dispatcher)
                .putLong(CALLBACK_HANDLE, handle)
                .apply()
        }

        fun getDispatcherHandle(context: Context): Long =
            context.getSharedPreferences(INTERNAL_PREFERENCES, Context.MODE_PRIVATE)
                .getLong(CALLBACK_DISPATCHER_HANDLE, 0)

        fun getHandle(context: Context): Long =
            context.getSharedPreferences(INTERNAL_PREFERENCES, Context.MODE_PRIVATE)
                .getLong(CALLBACK_HANDLE, 0)

        fun getData(context: Context): SharedPreferences =
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    // Activity Aware
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        GoogleMobileAdsPlugin.unregisterNativeAdFactory(flutterEngine, "listTile")
        GoogleMobileAdsPlugin.unregisterNativeAdFactory(flutterEngine, "listTileMedium")
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addOnNewIntentListener(this)
    }

    override fun onDetachedFromActivity() {
        unregisterReceiver()
        activity = null
    }

    override fun onDetachedFromActivityForConfigChanges() {
        unregisterReceiver()
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addOnNewIntentListener(this)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        receiver = createReceiver(events)
    }

    override fun onCancel(arguments: Any?) {
        unregisterReceiver()
        receiver = null
    }

    private fun createReceiver(events: EventChannel.EventSink?): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action.equals(HomeWidgetLaunchIntent.HOME_WIDGET_LAUNCH_ACTION)) {
                    events?.success(intent?.data?.toString() ?: true)
                }
            }

        }
    }

    private fun unregisterReceiver() {
        try {
            if (receiver != null) {
                context.unregisterReceiver(receiver)
            }
        } catch (e: IllegalArgumentException) {
            // Receiver not registered
        }
    }

    override fun onNewIntent(intent: Intent): Boolean {
        receiver?.onReceive(context, intent)
        return receiver != null
    }

    private fun loadAd() {
        Log.d(TAG, "Google SDK Initialized")

        val videoOptions: VideoOptions = VideoOptions.Builder()
            .setStartMuted(false)
            .build()

        val adOptions: NativeAdOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()


        val adLoader: AdLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/1044960115")
            .forNativeAd { nativeAd ->
                {
                    Log.d(TAG, "Native Ad Loaded")
                    if (activity!!.isDestroyed) {
                        nativeAd.destroy()
                        Log.d(TAG, "Native Ad Destroyed")
                    } else {

                        if (nativeAd.getMediaContent()!!.hasVideoContent()) {
                            nativeAd.getMediaContent()!!.getVideoController()
                                .setVideoLifecycleCallbacks(object :
                                    VideoController.VideoLifecycleCallbacks() {
                                    override fun onVideoEnd() {
                                        super.onVideoEnd()
                                        Log.d(TAG, "VideoEnd")
                                    }

                                    override fun onVideoMute(b: Boolean) {
                                        super.onVideoMute(b)
                                        Log.d(TAG, "VideoMute : $b")
                                    }

                                    override fun onVideoPause() {
                                        super.onVideoPause()
                                        Log.d(TAG, "VideoPause")
                                    }

                                    override fun onVideoPlay() {
                                        super.onVideoPlay()

                                        Log.d(TAG, "VideoPlay")
                                    }

                                    override fun onVideoStart() {
                                        super.onVideoStart()
                                        Log.d(TAG, "VideoStart")
                                    }
                                })
                        }

                        val styles: NativeTemplateStyle = NativeTemplateStyle.Builder().build()


                        template?.setStyles(styles)
                        template?.setVisibility(View.VISIBLE)
                        template?.setNativeAd(nativeAd)
                    }
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "Native Ad Failed To Load")
                    template?.setVisibility(View.GONE)

                    object : CountDownTimer(10000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            Log.d(TAG, "Sec : " + millisUntilFinished / 1000)
                        }

                        override fun onFinish() {
                            Log.d(TAG, "Reloading Native Ad")
                            loadAd()
                        }
                    }
                }
            })
            .withNativeAdOptions(adOptions)
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
}