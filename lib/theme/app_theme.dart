import 'package:flutter/material.dart';
import 'package:flutter_utils/theme/dark_theme.dart';
import 'package:flutter_utils/theme/light_color.dart';
import 'package:flutter_utils/theme/theme_color.dart';
import 'package:get/get.dart';

class AppTheme {
  static final ThemeData lightTheme = ThemeData(brightness: Brightness.light);

  static final ThemeData _darkTheme = ThemeData(brightness: Brightness.dark);

  static final LightColor _lightColor = LightColor();
  static final DarkColor _darkColor = DarkColor();

  static ThemeColor themeColor = _lightColor;

  static void changeThemeMode({ThemeMode themeMode = ThemeMode.system}) {
    switch (themeMode) {
      case ThemeMode.system:
        if (Get.isDarkMode) {
          Get.changeTheme(lightTheme);
          themeColor = _lightColor;
        } else {
          Get.changeTheme(_darkTheme);
          themeColor = _darkColor;
        }
        break;
      case ThemeMode.dark:
        Get.changeTheme(_darkTheme);
        themeColor = _darkColor;
        break;
      case ThemeMode.light:
        Get.changeTheme(lightTheme);
        themeColor = _lightColor;
        break;
    }
    Get.forceAppUpdate();
  }
}
