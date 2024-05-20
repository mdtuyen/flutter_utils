package com.example.flutter_native_ad

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.util.Map
import io.flutter.plugins.googlemobileads.GoogleMobileAdsPlugin

internal class NativeAdFactoryMedium(context: Context?) : GoogleMobileAdsPlugin.NativeAdFactory {
    private val context: Context? = context


    fun createNativeAd(nativeAd: NativeAd?, customOptions: Map<String?, Object?>?): NativeAdView? {
        val nativeAdView: NativeAdView = LayoutInflater.from(context)
            .inflate(R.layout.medium_template, null) as NativeAdView


        //    attribution
        val attributionViewSmall: TextView = nativeAdView
            .findViewById(R.id.native_ad_attribution_small)


        attributionViewSmall.setVisibility(View.VISIBLE)

        // icon
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.native_ad_icon))
        if (nativeAd.getIcon() == null) {
            nativeAdView.getIconView().setVisibility(View.GONE)
        } else {
            (nativeAdView.getIconView() as ImageView).setImageDrawable(
                nativeAd.getIcon().getDrawable()
            )
        }

        //  media
        val mediaView: MediaView = nativeAdView.findViewById(R.id.native_ad_media)
        mediaView.setMediaContent(nativeAd.getMediaContent())
        nativeAdView.setMediaView(mediaView)

        // button
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.native_ad_button))
        if (nativeAd.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE)
        } else {
            (nativeAdView.getCallToActionView() as Button).setText(nativeAd.getCallToAction())
        }

        //   headline
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.native_ad_headline))
        (nativeAdView.getHeadlineView() as TextView).setText(nativeAd.getHeadline())

        //  bodyView
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.native_ad_body))
        if (nativeAd.getBody() == null) {
            nativeAdView.getBodyView().setVisibility(View.INVISIBLE)
        } else {
            (nativeAdView.getBodyView() as TextView).setText(nativeAd.getBody())
            nativeAdView.getBodyView().setVisibility(View.VISIBLE)
        }

        //    advertiser name
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.native_ad_advertiser))
        if (nativeAd.getAdvertiser() == null) {
            nativeAdView.getAdvertiserView().setVisibility(View.GONE)
        } else {
            (nativeAdView.getAdvertiserView() as TextView).setText(nativeAd.getAdvertiser())
            nativeAdView.getAdvertiserView().setVisibility(View.VISIBLE)
        }
        //   ratingbar
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.native_ad_rating))
        if (nativeAd.getStarRating() == null) {
            nativeAdView.getStarRatingView().setVisibility(View.INVISIBLE)
        } else {
            (nativeAdView.getStarRatingView() as RatingBar).setRating(
                nativeAd.getStarRating().floatValue()
            )
            nativeAdView.getStarRatingView().setVisibility(View.VISIBLE)
        }




        nativeAdView.setNativeAd(nativeAd)

        return nativeAdView
    }
}
