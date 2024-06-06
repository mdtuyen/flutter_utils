import 'package:flutter/material.dart';
import 'package:flutter_utils/theme/app_theme.dart';
import 'package:flutter_utils/utils/extension/extension_util.dart';
import 'package:flutter_utils/widget/image_extended.dart';
import 'package:flutter_utils/widget/text/text_common.dart';

class IconTextWidget extends StatelessWidget {
  final String iconUrl;
  final String text;
  final double? iconWidth;
  final double? iconHeight;
  final double? interval;
  final double? textSize;
  final Color? textColor;

  const IconTextWidget(
      {Key? key,
      required this.iconUrl,
      required this.text,
      this.iconWidth,
      this.iconHeight,
      this.interval,
      this.textSize,
      this.textColor})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ImageCommon(
          iconUrl,
          width: iconWidth ?? 20.width,
          height: iconHeight ?? 20.height,
        ),
        (interval ?? 10).heightBox,
        TextCommon(
          text,
          size: textSize,
          color: textColor ?? AppTheme.themeColor.textPrimary,
        )
      ],
    );
  }
}
