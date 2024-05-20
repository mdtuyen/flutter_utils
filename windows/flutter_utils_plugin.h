#ifndef FLUTTER_PLUGIN_FLUTTER_UTILS_PLUGIN_H_
#define FLUTTER_PLUGIN_FLUTTER_UTILS_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace flutter_utils {

class FlutterUtilsPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  FlutterUtilsPlugin();

  virtual ~FlutterUtilsPlugin();

  // Disallow copy and assign.
  FlutterUtilsPlugin(const FlutterUtilsPlugin&) = delete;
  FlutterUtilsPlugin& operator=(const FlutterUtilsPlugin&) = delete;

  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace flutter_utils

#endif  // FLUTTER_PLUGIN_FLUTTER_UTILS_PLUGIN_H_
