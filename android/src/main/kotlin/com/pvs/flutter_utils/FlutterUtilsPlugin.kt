package com.pvs.flutter_utils

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugins.googlemobileads.GoogleMobileAdsPlugin
import io.flutter.embedding.engine.FlutterEngine

/** FlutterUtilsPlugin */
class FlutterUtilsPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  var pluginBinding: FlutterPluginBinding? = null
  private var flutterEngine: FlutterEngine? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_utils")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.getApplicationContext()
    flutterEngine = flutterPluginBinding.getFlutterEngine()
    pluginBinding = flutterPluginBinding
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }else if (call.method.equals("getNativeAds")) {
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

      val arguments: Map<String, String> = call.arguments()
      val nativeId = arguments["nativeVideoID"]!!

      pluginBinding.getPlatformViewRegistry()
        .registerViewFactory("<platform-view-type>", NativeVideoViewFactory(nativeId))
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    GoogleMobileAdsPlugin.unregisterNativeAdFactory(flutterEngine, "listTile")
    GoogleMobileAdsPlugin.unregisterNativeAdFactory(flutterEngine, "listTileMedium")
    channel.setMethodCallHandler(null)
  }
}
