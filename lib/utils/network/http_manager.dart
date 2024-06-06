import 'dart:io';

import 'package:dio/dio.dart';
import 'package:dio/io.dart';
import 'package:flutter_utils/config/build_config.dart';
import 'package:flutter_utils/config/environment.dart';
import 'package:flutter_utils/utils/network/pretty_dio_logger.dart';

import 'base_remote_source.dart';

abstract class HttpMethods {
  static const String post = "POST";
  static const String get = "GET";
  static const String put = "PUT";
  static const String patch = "PATCH";
  static const String delete = "DELETE";
}

class HttpManager extends BaseRemoteSource {
  static final String baseUrl = BuildConfig.instance.config.baseUrl;
  static const int _maxLineWidth = 90;
  static final _prettyDioLogger = PrettyDioLogger(
      requestHeader: true,
      requestBody: true,
      responseBody: BuildConfig.instance.environment == Environment.DEVELOPMENT,
      responseHeader: false,
      error: true,
      compact: true,
      maxWidth: _maxLineWidth);

  Future restRequest(
    String url,
    String method, {
    Map? headers,
    BaseOptions? options,
    Object? data,
    Map<String, dynamic>? queryParameters,
    CancelToken? cancelToken,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final headersDefault = headers?.cast<String, String>() ?? {}
      ..addAll({
        'content-type': 'application/json',
        'accept': 'application/json',
      });
    Dio dio = getDio(options);
    try {
      return callApiWithErrorParser(dio.request(url,
          options: Options(method: method, headers: headersDefault),
          data: data,
          queryParameters: queryParameters,
          cancelToken: cancelToken,
          onSendProgress: onSendProgress,
          onReceiveProgress: onReceiveProgress));
    } catch (e) {
      rethrow;
    }
  }

  static Dio? _instance;

  static Dio getDio([BaseOptions? options]) {
    if (_instance == null) {
      final optionsDefault = options ??
          BaseOptions(
            baseUrl: baseUrl,
            connectTimeout: BuildConfig.instance.config.connectTimeout,
            sendTimeout: BuildConfig.instance.config.sendTimeout,
            receiveTimeout: BuildConfig.instance.config.receiveTimeout,
          );
      _instance = Dio(optionsDefault);
    } else {
      _instance!.interceptors.clear();
      if (options != null) {
        _instance!.options = options;
      }
    }
    if (BuildConfig.isDebug) _instance!.interceptors.add(_prettyDioLogger);
    if (BuildConfig.instance.config.interceptors?.isNotEmpty ?? false) {
      _instance!.interceptors
          .addAll(BuildConfig.instance.config.interceptors as Iterable<Interceptor>);
    }
    if (BuildConfig.instance.config.proxy?.isNotEmpty ?? false) {
      _instance!.httpClientAdapter = IOHttpClientAdapter(createHttpClient: () {
        final client = HttpClient();
        client.findProxy = (uri) {
          return "PROXY ${BuildConfig.instance.config.proxy}";
        };
        client.badCertificateCallback =
            (X509Certificate cert, String host, int port) => true;
        return client;
      });
    }
    return _instance!;
  }
}
