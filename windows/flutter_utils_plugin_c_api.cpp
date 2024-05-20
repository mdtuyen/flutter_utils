#include "include/flutter_utils/flutter_utils_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "flutter_utils_plugin.h"

void FlutterUtilsPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  flutter_utils::FlutterUtilsPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
