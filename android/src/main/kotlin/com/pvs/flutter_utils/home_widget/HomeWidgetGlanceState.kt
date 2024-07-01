package com.pvs.flutter_utils.home_widget

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.datastore.core.DataStore
import androidx.glance.state.GlanceStateDefinition
import com.pvs.flutter_utils.FlutterUtilsPlugin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class HomeWidgetGlanceState(val preferences: SharedPreferences)

class HomeWidgetGlanceStateDefinition : GlanceStateDefinition<HomeWidgetGlanceState> {
    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<HomeWidgetGlanceState> {
        val preferences = context.getSharedPreferences(FlutterUtilsPlugin.PREFERENCES, Context.MODE_PRIVATE)
        return HomeWidgetGlanceDataStore(preferences)
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return Environment.getDataDirectory()
    }

}

private class HomeWidgetGlanceDataStore(private val preferences: SharedPreferences) : DataStore<HomeWidgetGlanceState> {
    override val data: Flow<HomeWidgetGlanceState>
        get() = flow { emit(HomeWidgetGlanceState(preferences)) }

    override suspend fun updateData(transform: suspend (t: HomeWidgetGlanceState) -> HomeWidgetGlanceState): HomeWidgetGlanceState {
        return transform(HomeWidgetGlanceState(preferences))
    }
}