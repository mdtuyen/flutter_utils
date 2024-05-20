package com.example.flutter_native_ad

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import java.util.Map
import io.flutter.plugin.platform.PlatformView

class NativeVideoView internal constructor(
    @NonNull context: Context?,
    id: Int,
    @Nullable creationParams: Map<String?, Object?>?,
    videoid: String?
) : PlatformView {
    @NonNull
    private val templateView: View? =
        LayoutInflater.from(context).inflate(R.layout.activity_main, null)
    private val template: TemplateView? = templateView.findViewById(R.id.my_template)


    init {
        //---> initializing Google Ad SDK
        MobileAds.initialize(context, object : OnInitializationCompleteListener() {
            @Override
            fun onInitializationComplete(initializationStatus: InitializationStatus?) {
                loadAd(context, videoid)
            }
        })
    }


    private fun loadAd(context: Context?, videoid: String?) {
        Log.d(TAG, "Google SDK Initialized")

        val videoOptions: VideoOptions = Builder()
            .setStartMuted(false)
            .build()

        val adOptions: NativeAdOptions = Builder()
            .setVideoOptions(videoOptions)
            .build()


        val adLoader: AdLoader = Builder(context, videoid)
            .forNativeAd(object : OnNativeAdLoadedListener() {
                @Override
                fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    Log.d(TAG, "Native Ad Loaded")
                    if (nativeAd.getMediaContent().hasVideoContent()) {
                        val mediaAspectRatio: Float = nativeAd.getMediaContent().getAspectRatio()
                        val duration: Float = nativeAd.getMediaContent().getDuration()

                        nativeAd.getMediaContent().getVideoController()
                            .setVideoLifecycleCallbacks(object : VideoLifecycleCallbacks() {
                                @Override
                                fun onVideoEnd() {
                                    super.onVideoEnd()
                                    Log.d(TAG, "VideoEnd")
                                }

                                @Override
                                fun onVideoMute(b: Boolean) {
                                    super.onVideoMute(b)
                                    Log.d(TAG, "VideoMute : $b")
                                }

                                @Override
                                fun onVideoPause() {
                                    super.onVideoPause()
                                    Log.d(TAG, "VideoPause")
                                }

                                @Override
                                fun onVideoPlay() {
                                    super.onVideoPlay()

                                    Log.d(TAG, "VideoPlay")
                                }

                                @Override
                                fun onVideoStart() {
                                    super.onVideoStart()
                                    Log.d(TAG, "VideoStart")
                                }
                            })
                    }

                    val styles: NativeTemplateStyle = Builder().build()


                    template.setStyles(styles)
                    template.setVisibility(View.VISIBLE)
                    template.setNativeAd(nativeAd)
                }
            })

            .withAdListener(object : AdListener() {
                @Override
                fun onAdFailedToLoad(adError: LoadAdError?) {
                    // Handle the failure by logging, altering the UI, and so on.
                    Log.d(TAG, "Native Ad Failed To Load")
                    template.setVisibility(View.GONE)
                    object : CountDownTimer(10000, 1000) {
                        @Override
                        fun onTick(millisUntilFinished: Long) {
                            Log.d(TAG, "Sec : " + millisUntilFinished / 1000)
                        }

                        @Override
                        fun onFinish() {
                            Log.d(TAG, "Reloading Native Ad")
                            loadAd(context, videoid)
                        }
                    }
                }
            })
            .withNativeAdOptions(adOptions)
            .build()

        adLoader.loadAd(Builder().build())
    }


    @Nullable
    @Override
    fun getView(): View? {
        return templateView
    }

    @Override
    fun dispose() {
    }

    companion object {
        private val TAG: String? = "-->Native"
    }
}