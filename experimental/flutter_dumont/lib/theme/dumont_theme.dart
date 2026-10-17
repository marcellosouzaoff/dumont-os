import 'package:flutter/material.dart';

/// Paleta 01; os valores refletem design/tokens.json.
abstract final class DumontTheme {
  static const Color baritoneBlue = Color(0xFF272836);
  static const Color cloudDancer = Color(0xFFF0EEE9);
  static const Color curdsAndWhey = Color(0xFFBCA483);
  static const Color ink = Color(0xFF373945);
  static const Color muted = Color(0xFFAEB0BF);
  static const Color white = Color(0xFFFFFFFF);

  static const Color stateSuccess = Color(0xFF287D53);
  static const Color stateWarning = Color(0xFFB78319);
  static const Color stateError = Color(0xFFCD212A);
  static const Color stateUnknown = Color(0xFF626474);

  static ThemeData get darkTheme => ThemeData(
    useMaterial3: true,
    scaffoldBackgroundColor: baritoneBlue,
    fontFamily: 'DM Sans',
    colorScheme: ColorScheme.fromSeed(
      seedColor: baritoneBlue,
      surface: cloudDancer,
    ),
  );
}
