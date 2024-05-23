import 'package:dio/dio.dart';
import 'package:flutter_utils/mvvm/core/network/pretty_dio_logger.dart';
import 'package:flutter_utils/mvvm/flavors/build_config.dart';
import 'package:flutter_utils/mvvm/flavors/environment.dart';


class DioProvider {
  static final String baseUrl = BuildConfig.instance.config.baseUrl;

  static Dio? _instance;

  static const int _maxLineWidth = 90;
  static final _prettyDioLogger = PrettyDioLogger(
      requestHeader: true,
      requestBody: true,
      responseBody: BuildConfig.instance.environment == Environment.DEVELOPMENT,
      responseHeader: false,
      error: true,
      compact: true,
      maxWidth: _maxLineWidth);

  static final BaseOptions _options = BaseOptions(
    baseUrl: baseUrl,
    connectTimeout: const Duration(seconds: 60),
    receiveTimeout: const Duration(seconds: 60),
  );

  static Dio get httpDio {
    if (_instance == null) {
      _instance = Dio(_options);
    } else {
      _instance!.interceptors.clear();
    }
    _instance!.interceptors.add(_prettyDioLogger);
    return _instance!;
  }

  ///returns a Dio client with Access token in header
  static Dio get dioWithHeaderToken {
    _addInterceptors();
    return _instance!;
  }

  static _addInterceptors() {
    _instance ??= httpDio;
    _instance!.interceptors.clear();
    _instance!.interceptors.add(RequestHeaderInterceptor());
    _instance!.interceptors.add(_prettyDioLogger);
  }
}
class RequestHeaderInterceptor extends InterceptorsWrapper {
  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    getCustomHeaders().then((customHeaders) {
      options.headers.addAll(customHeaders);
      super.onRequest(options, handler);
    });
  }

  Future<Map<String, String>> getCustomHeaders() async {
    var customHeaders = {'content-type': 'application/json', 'accept': 'application/json'};

    return customHeaders;
  }
}
