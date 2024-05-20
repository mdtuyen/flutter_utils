package com.example.flutter_native_ad

import android.content.Context
import java.util.Map
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class NativeVideoViewFactory internal constructor(var videoid: String?) :
    PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    @NonNull
    @Override
    fun create(@NonNull context: Context?, id: Int, @Nullable args: Object?): PlatformView? {
        val creationParams: Map<String?, Object?>? = args as Map<String?, Object?>?

        return NativeVideoView(context, id, creationParams, videoid)
    }
}
