import 'package:flutter_utils/config/env_config.dart';
import 'package:flutter_utils/config/environment.dart';
import 'package:flutter/foundation.dart' as Foundation;

class BuildConfig {
  late final Environment environment;
  late final EnvConfig config;
  bool _lock = false;
  static bool isDebug =
      Foundation.kDebugMode || BuildConfig.instance.environment == Environment.DEVELOPMENT;
  static final BuildConfig instance = BuildConfig._internal();

  BuildConfig._internal();

  factory BuildConfig.instantiate({
    required Environment envType,
    required EnvConfig envConfig,
  }) {
    if (instance._lock) return instance;

    instance.environment = envType;
    instance.config = envConfig;
    instance._lock = true;

    return instance;
  }
}
