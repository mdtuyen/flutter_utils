import 'package:dio/dio.dart';
import 'package:flutter_utils/mvvm/core/network/error_handlers.dart';
import 'package:flutter_utils/mvvm/core/network/exceptions/base_exception.dart';
import 'package:flutter_utils/mvvm/flavors/build_config.dart';
import 'package:get/get_connect/http/src/status/http_status.dart';

abstract class BaseRemoteSource {
  final logger = BuildConfig.instance.config.logger;

  Future<Response<T>> callApiWithErrorParser<T>(Future<Response<T>> api) async {
    try {
      return await api;
    } on DioException catch (dioError) {
      Exception exception = handleDioError(dioError);
      logger.e(
          "Throwing error from repository: >>>>>>> $exception : ${(exception as BaseException).message}");
      throw exception;
    } catch (error) {
      logger.e("Generic error: >>>>>>> $error");

      if (error is BaseException) {
        rethrow;
      }

      throw handleError("$error");
    }
  }
}
