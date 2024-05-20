
import 'flutter_utils_platform_interface.dart';

class FlutterUtils {
  Future<String?> getPlatformVersion() {
    return FlutterUtilsPlatform.instance.getPlatformVersion();
  }
}
