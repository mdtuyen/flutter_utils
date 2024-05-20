// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.flutter_native_ad

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * Base class for a template view. *
 */
class TemplateView : FrameLayout {
    private var templateType = 0
    private var styles: NativeTemplateStyle? = null
    private var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null
    private var primaryView: TextView? = null
    private var secondaryView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var tertiaryView: TextView? = null
    private var iconView: ImageView? = null
    private var mediaView: MediaView? = null
    private var callToActionView: Button? = null
    private var background: ConstraintLayout? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    @SuppressLint("NewApi")
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    fun setStyles(styles: NativeTemplateStyle?) {
        this.styles = styles
        this.applyStyles()
    }

    fun getNativeAdView(): NativeAdView? {
        return nativeAdView
    }

    private fun applyStyles() {
        val mainBackground: Drawable = styles.getMainBackgroundColor()
        if (mainBackground != null) {
            background.setBackground(mainBackground)
            if (primaryView != null) {
                primaryView.setBackground(mainBackground)
            }
            if (secondaryView != null) {
                secondaryView.setBackground(mainBackground)
            }
            if (tertiaryView != null) {
                tertiaryView.setBackground(mainBackground)
            }
        }

        val primary: Typeface = styles.getPrimaryTextTypeface()
        if (primary != null && primaryView != null) {
            primaryView.setTypeface(primary)
        }

        val secondary: Typeface = styles.getSecondaryTextTypeface()
        if (secondary != null && secondaryView != null) {
            secondaryView.setTypeface(secondary)
        }

        val tertiary: Typeface = styles.getTertiaryTextTypeface()
        if (tertiary != null && tertiaryView != null) {
            tertiaryView.setTypeface(tertiary)
        }

        val ctaTypeface: Typeface = styles.getCallToActionTextTypeface()
        if (ctaTypeface != null && callToActionView != null) {
            callToActionView.setTypeface(ctaTypeface)
        }

        val primaryTypefaceColor: Int = styles.getPrimaryTextTypefaceColor()
        if (primaryTypefaceColor > 0 && primaryView != null) {
            primaryView.setTextColor(primaryTypefaceColor)
        }

        val secondaryTypefaceColor: Int = styles.getSecondaryTextTypefaceColor()
        if (secondaryTypefaceColor > 0 && secondaryView != null) {
            secondaryView.setTextColor(secondaryTypefaceColor)
        }

        val tertiaryTypefaceColor: Int = styles.getTertiaryTextTypefaceColor()
        if (tertiaryTypefaceColor > 0 && tertiaryView != null) {
            tertiaryView.setTextColor(tertiaryTypefaceColor)
        }

        val ctaTypefaceColor: Int = styles.getCallToActionTypefaceColor()
        if (ctaTypefaceColor > 0 && callToActionView != null) {
            callToActionView.setTextColor(ctaTypefaceColor)
        }

        val ctaTextSize: Float = styles.getCallToActionTextSize()
        if (ctaTextSize > 0 && callToActionView != null) {
            callToActionView.setTextSize(ctaTextSize)
        }

        val primaryTextSize: Float = styles.getPrimaryTextSize()
        if (primaryTextSize > 0 && primaryView != null) {
            primaryView.setTextSize(primaryTextSize)
        }

        val secondaryTextSize: Float = styles.getSecondaryTextSize()
        if (secondaryTextSize > 0 && secondaryView != null) {
            secondaryView.setTextSize(secondaryTextSize)
        }

        val tertiaryTextSize: Float = styles.getTertiaryTextSize()
        if (tertiaryTextSize > 0 && tertiaryView != null) {
            tertiaryView.setTextSize(tertiaryTextSize)
        }

        val ctaBackground: Drawable = styles.getCallToActionBackgroundColor()
        if (ctaBackground != null && callToActionView != null) {
            callToActionView.setBackground(ctaBackground)
        }

        val primaryBackground: Drawable = styles.getPrimaryTextBackgroundColor()
        if (primaryBackground != null && primaryView != null) {
            primaryView.setBackground(primaryBackground)
        }

        val secondaryBackground: Drawable = styles.getSecondaryTextBackgroundColor()
        if (secondaryBackground != null && secondaryView != null) {
            secondaryView.setBackground(secondaryBackground)
        }

        val tertiaryBackground: Drawable = styles.getTertiaryTextBackgroundColor()
        if (tertiaryBackground != null && tertiaryView != null) {
            tertiaryView.setBackground(tertiaryBackground)
        }

        invalidate()
        requestLayout()
    }

    private fun adHasOnlyStore(nativeAd: NativeAd?): Boolean {
        val store: String = nativeAd.getStore()
        val advertiser: String = nativeAd.getAdvertiser()
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)
    }

    fun setNativeAd(nativeAd: NativeAd?) {
        this.nativeAd = nativeAd

        val store: String = nativeAd.getStore()
        val advertiser: String = nativeAd.getAdvertiser()
        val headline: String = nativeAd.getHeadline()
        val body: String = nativeAd.getBody()
        val cta: String = nativeAd.getCallToAction()
        val starRating: Double = nativeAd.getStarRating()
        val icon: NativeAd.Image = nativeAd.getIcon()

        val secondaryText: String

        nativeAdView.setCallToActionView(callToActionView)
        nativeAdView.setHeadlineView(primaryView)
        nativeAdView.setMediaView(mediaView)
        secondaryView.setVisibility(VISIBLE)
        if (adHasOnlyStore(nativeAd)) {
            nativeAdView.setStoreView(secondaryView)
            secondaryText = store
        } else if (!TextUtils.isEmpty(advertiser)) {
            nativeAdView.setAdvertiserView(secondaryView)
            secondaryText = advertiser
        } else {
            secondaryText = ""
        }

        primaryView.setText(headline)
        callToActionView.setText(cta)

        //  Set the secondary view to be the star rating if available.
        if (starRating != null && starRating > 0) {
            secondaryView.setVisibility(GONE)
            ratingBar.setVisibility(VISIBLE)
            ratingBar.setMax(5)
            nativeAdView.setStarRatingView(ratingBar)
        } else {
            secondaryView.setText(secondaryText)
            secondaryView.setVisibility(VISIBLE)
            ratingBar.setVisibility(GONE)
        }

        if (icon != null) {
            iconView.setVisibility(VISIBLE)
            iconView.setImageDrawable(icon.getDrawable())
        } else {
            iconView.setVisibility(GONE)
        }

        if (tertiaryView != null) {
            tertiaryView.setText(body)
            nativeAdView.setBodyView(tertiaryView)
        }

        nativeAdView.setNativeAd(nativeAd)
    }

    /**
     * To prevent memory leaks, make sure to destroy your ad when you don't need it anymore. This
     * method does not destroy the template view.
     * https://developers.google.com/admob/android/native-unified#destroy_ad
     */
    fun destroyNativeAd() {
        nativeAd.destroy()
    }

    fun getTemplateTypeName(): String? {
        if (templateType == R.layout.gnt_medium_template_view) {
            return MEDIUM_TEMPLATE
        } else if (templateType == R.layout.gnt_small_template_view) {
            return SMALL_TEMPLATE
        }
        return ""
    }

    private fun initView(context: Context?, attributeSet: AttributeSet?) {
        val attributes: TypedArray =
            context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0)

        try {
            templateType =
                attributes.getResourceId(
                    R.styleable.TemplateView_gnt_template_type, R.layout.gnt_medium_template_view
                )
        } finally {
            attributes.recycle()
        }
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(templateType, this)
    }

    @Override
    fun onFinishInflate() {
        super.onFinishInflate()
        nativeAdView = findViewById(R.id.native_ad_view) as NativeAdView?
        primaryView = findViewById(R.id.primary) as TextView?
        secondaryView = findViewById(R.id.secondary) as TextView?
        tertiaryView = findViewById(R.id.body) as TextView?

        ratingBar = findViewById(R.id.rating_bar) as RatingBar?
        ratingBar.setEnabled(false)

        callToActionView = findViewById(R.id.cta) as Button?
        iconView = findViewById(R.id.icon) as ImageView?
        mediaView = findViewById(R.id.media_view) as MediaView?
        background = findViewById(R.id.background) as ConstraintLayout?
    }

    companion object {
        private val MEDIUM_TEMPLATE: String? = "medium_template"
        private val SMALL_TEMPLATE: String? = "small_template"
    }
}
