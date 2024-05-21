import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_utils_platform_interface.dart';

/// An implementation of [FlutterUtilsPlatform] that uses method channels.
class MethodChannelFlutterUtils extends FlutterUtilsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_utils');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future getNativeAds() async {
    await methodChannel.invokeMethod<String>('getNativeAds');
  }
}
