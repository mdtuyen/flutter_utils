import 'package:dio/dio.dart' show Dio, ResponseType;
import 'package:flutter_utils/mvvm/network/dio_interceptor.dart';
import 'package:flutter_utils/mvvm/res/api_config.dart';

class DioClient {
  final Dio dio;

  DioClient(this.dio) {
    dio
      ..options.baseUrl = ApiConfig.baseUrl
      ..options.headers = ApiConfig.header
      ..options.connectTimeout = ApiConfig.connectionTimeout
      ..options.receiveTimeout = ApiConfig.receiveTimeout
      ..options.responseType = ResponseType.json
      ..interceptors.add(DioInterceptor());
  }
}
