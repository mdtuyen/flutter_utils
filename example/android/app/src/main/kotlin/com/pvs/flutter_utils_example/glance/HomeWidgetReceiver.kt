package com.pvs.flutter_utils_example.glance

import com.pvs.flutter_utils.home_widget.HomeWidgetGlanceWidgetReceiver

class HomeWidgetReceiver : HomeWidgetGlanceWidgetReceiver<HomeWidgetGlanceAppWidget>() {
    override val glanceAppWidget = HomeWidgetGlanceAppWidget()
}