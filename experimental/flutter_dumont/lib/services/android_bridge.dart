import 'package:flutter/services.dart';

/// Dados vindos exclusivamente da consulta Android MAIN/LAUNCHER.
class NativeApp {
  const NativeApp({
    required this.label,
    required this.packageName,
    required this.component,
  });

  final String label;
  final String packageName;
  final String component;

  static NativeApp? tryParse(Object? input) {
    if (input is! Map) return null;
    final label = input['label'];
    final packageName = input['packageName'];
    final component = input['component'];
    if (label is! String ||
        label.trim().isEmpty ||
        packageName is! String ||
        packageName.trim().isEmpty ||
        component is! String ||
        component.trim().isEmpty) {
      return null;
    }
    return NativeApp(
      label: label.trim(),
      packageName: packageName.trim(),
      component: component.trim(),
    );
  }
}

/// Contrato v1: métodos públicos sem permissões privilegiadas.
/// Erros como MissingPluginException chegam ao chamador para explicitar fallback.
class DumontNativeBridge {
  const DumontNativeBridge();

  static const MethodChannel _channel = MethodChannel(
    'br.com.dumont.experience/native',
  );

  Future<List<NativeApp>> listApps() async {
    final raw = await _channel.invokeMethod<List<dynamic>>('listApps');
    final unique = <String, NativeApp>{};
    for (final value in raw ?? const <dynamic>[]) {
      final app = NativeApp.tryParse(value);
      if (app != null) unique[app.component] = app;
    }
    return unique.values.toList(growable: false);
  }

  Future<bool> launchApp(String component) async {
    if (component.trim().isEmpty) return false;
    return await _channel.invokeMethod<bool>('launchApp', <String, String>{
          'component': component,
        }) ??
        false;
  }
}
