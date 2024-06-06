import 'package:get/get_connect/http/src/status/http_status.dart';
class AppException extends BaseException {
  AppException({super.message});
}
class ApiException extends BaseApiException {
  ApiException({
    required super.httpCode,
    required super.status,
    super.message,
  });
}
class NotFoundException extends ApiException {
  NotFoundException(String message, String status)
      : super(httpCode: HttpStatus.notFound, status: status, message: message);
}
class ServiceUnavailableException extends BaseApiException {
  ServiceUnavailableException(String message)
      : super(
      httpCode: HttpStatus.serviceUnavailable,
      message: message,
      status: "");
}
class TimeoutException extends BaseException {
  TimeoutException(String message) : super(message: message);
}
class UnauthorizedException extends BaseApiException {
  UnauthorizedException(String message)
      : super(
      httpCode: HttpStatus.unauthorized,
      message: message,
      status: "unauthorized");
}
class NetworkException extends BaseException {
  NetworkException(String message) : super(message: message);
}
class JsonFormatException extends BaseException {
  JsonFormatException(String message) : super(message: message);
}
abstract class BaseApiException extends AppException {
  final int httpCode;
  final String status;
  BaseApiException({this.httpCode = -1, this.status = "", super.message});
}
abstract class BaseException implements Exception {
  final String message;
  BaseException({this.message = ""});
}