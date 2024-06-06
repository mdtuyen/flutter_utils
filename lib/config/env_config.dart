import 'package:dio/dio.dart';
import 'package:flutter_utils/theme/app_values.dart';
import 'package:logger/logger.dart';

class EnvConfig {
  final String appName;
  final String baseUrl;
  final Duration receiveTimeout;
  final Duration connectTimeout;
  final Duration? sendTimeout;
  final bool shouldCollectCrashLog;
  final String? proxy;
  final String? cookiesPath;
  final List<Interceptor>? interceptors;

  late final Logger logger;

  EnvConfig(
      {required this.appName,
      required this.baseUrl,
      this.proxy,
      this.cookiesPath,
      this.interceptors,
      this.shouldCollectCrashLog = false,
      this.receiveTimeout = const Duration(milliseconds: 15000),
      this.sendTimeout = const Duration(milliseconds: 15000),
      this.connectTimeout = const Duration(milliseconds: 15000)}) {
    logger = Logger(
      printer: PrettyPrinter(
          methodCount: AppValues.loggerMethodCount,
          // number of method calls to be displayed
          errorMethodCount: AppValues.loggerErrorMethodCount,
          // number of method calls if stacktrace is provided
          lineLength: AppValues.loggerLineLength,
          // width of the output
          colors: true,
          // Colorful log messages
          printEmojis: true,
          // Print an emoji for each log message
          printTime: false // Should each log print contain a timestamp
          ),
    );
  }
}
