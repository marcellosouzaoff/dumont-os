import 'package:flutter/services.dart';

/// Dados vindos exclusivamente da consulta Android MAIN/LAUNCHER.
class NativeApp {
  const NativeApp({
    required this.label,
    required this.packageName,
    required this.component,
    this.iconPng,
  });

  final String label;
  final String packageName;
  final String component;
  final Uint8List? iconPng;

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
      iconPng: input['iconPng'] is Uint8List
          ? input['iconPng'] as Uint8List
          : null,
    );
  }
}

/// Read-only snapshot: values unknown until the Android bridge responds.
class DumontDeviceInfo {
  const DumontDeviceInfo({
    required this.batteryPercent,
    required this.isCharging,
    required this.networkType,
    required this.model,
    required this.androidVersion,
  });

  final int? batteryPercent;
  final bool? isCharging;
  final String networkType;
  final String model;
  final String androidVersion;

  static DumontDeviceInfo parse(Object? raw) {
    final data = raw is Map ? raw : const {};
    final candidate = data['batteryPercent'];
    final battery = candidate is int && candidate >= 0 && candidate <= 100
        ? candidate
        : null;
    final candidateType = data['networkType'];
    const allowed = {
      'wifi',
      'cellular',
      'ethernet',
      'offline',
      'other',
      'unknown',
    };
    final transport = candidateType is String && allowed.contains(candidateType)
        ? candidateType
        : 'unknown';
    final model = data['model'];
    final version = data['androidVersion'];
    return DumontDeviceInfo(
      batteryPercent: battery,
      isCharging: data['isCharging'] is bool
          ? data['isCharging'] as bool
          : null,
      networkType: transport,
      model: model is String && model.trim().isNotEmpty
          ? model.trim()
          : 'Desconhecido',
      androidVersion: version is String && version.trim().isNotEmpty
          ? version.trim()
          : 'Desconhecido',
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

  static const publicSettingsIds = <String>{
    'internet',
    'wifi',
    'bluetooth',
    'display',
    'sound',
    'battery',
    'storage',
    'apps',
    'notifications',
    'privacy',
    'security',
    'accessibility',
    'vpn',
    'wallpaper',
  };

  Future<DumontDeviceInfo> getDeviceStatus() async {
    final data = await _channel.invokeMethod<dynamic>('getDeviceStatus');
    return DumontDeviceInfo.parse(data);
  }

  Future<bool> openSettings(String id) async {
    if (!publicSettingsIds.contains(id)) {
      throw ArgumentError.value(id, 'id', 'Unsupported public settings action');
    }
    return await _channel.invokeMethod<bool>('openSettings', <String, String>{
          'id': id,
        }) ??
        false;
  }

  Future<bool> launchApp(String component) async {
    if (component.trim().isEmpty) return false;
    return await _channel.invokeMethod<bool>('launchApp', <String, String>{
          'component': component,
        }) ??
        false;
  }
}
