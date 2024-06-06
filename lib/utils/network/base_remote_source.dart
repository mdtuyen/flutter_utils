import 'package:dio/dio.dart';
import 'package:flutter_utils/config/build_config.dart';
import 'package:flutter_utils/utils/network/error_handlers.dart';
import 'package:flutter_utils/utils/network/api_exception.dart';

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
