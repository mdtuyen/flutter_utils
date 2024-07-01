package com.pvs.flutter_utils.home_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import com.pvs.flutter_utils.FlutterUtilsPlugin

abstract class HomeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        onUpdate(context, appWidgetManager, appWidgetIds, FlutterUtilsPlugin.getData(context))
    }

    abstract fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, widgetData: SharedPreferences)
}