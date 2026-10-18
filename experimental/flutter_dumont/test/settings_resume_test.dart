import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/main.dart';
import 'package:dumont_experience/services/android_bridge.dart';

Map<String, Object> snapshot(int percent, {String transport = 'wifi'}) => {
  'batteryPercent': percent,
  'isCharging': false,
  'networkType': transport,
  'model': 'SM-X510',
  'androidVersion': '16',
};

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  const channel = MethodChannel('br.com.dumont.experience/native');
  final messenger =
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger;

  tearDown(() => messenger.setMockMethodCallHandler(channel, null));

  testWidgets('returning from Android settings refreshes stale device state', (
    tester,
  ) async {
    tester.view.physicalSize = const Size(1152, 720);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    var percent = 54;
    var statusReads = 0;
    var notificationReads = 0;
    final opened = <String>[];
    messenger.setMockMethodCallHandler(channel, (call) async {
      if (call.method == 'getDeviceStatus') {
        statusReads++;
        return snapshot(percent);
      }
      if (call.method == 'openSettings') {
        opened.add(call.arguments['id'] as String);
        return true;
      }
      if (call.method == 'listApps') return <Object>[];
      if (call.method == 'getNotificationSnapshot') {
        notificationReads++;
        return {
          'accessGranted': false,
          'connected': false,
          'notifications': <Object>[],
        };
      }
      return null;
    });
    await tester.pumpWidget(const DumontApp(ghostInitiallyOpen: true));
    await tester.pumpAndSettle();
    expect(find.text('54%'), findsOneWidget);
    expect(statusReads, 1);

    await tester.tap(find.text('Conexões'));
    await tester.pumpAndSettle();
    expect(opened, ['internet']);

    percent = 72;
    tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.inactive);
    tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.resumed);
    await tester.pumpAndSettle();
    expect(find.text('72%'), findsOneWidget);
    expect(find.text('54%'), findsNothing);
    expect(statusReads, 2);
    expect(notificationReads, greaterThanOrEqualTo(2));
  });

  testWidgets('late one-shot response cannot overwrite newer native event', (
    tester,
  ) async {
    tester.view.physicalSize = const Size(1152, 720);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    final updates = StreamController<DumontDeviceInfo>.broadcast(sync: true);
    addTearDown(updates.close);
    Completer<Map<String, Object>>? lateResponse;
    var statusReads = 0;
    messenger.setMockMethodCallHandler(channel, (call) async {
      if (call.method == 'getDeviceStatus') {
        statusReads++;
        if (statusReads == 1) return snapshot(54);
        lateResponse = Completer<Map<String, Object>>();
        return lateResponse!.future;
      }
      if (call.method == 'listApps') return <Object>[];
      if (call.method == 'getNotificationSnapshot') {
        return {
          'accessGranted': false,
          'connected': false,
          'notifications': <Object>[],
        };
      }
      return true;
    });

    await tester.pumpWidget(DumontApp(deviceStatusEvents: updates.stream));
    await tester.pumpAndSettle();
    expect(find.text('54%'), findsOneWidget);
    tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.inactive);
    tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.resumed);
    await tester.pump();
    expect(statusReads, 2);

    updates.add(DumontDeviceInfo.parse(snapshot(91, transport: 'cellular')));
    await tester.pump();
    expect(find.text('91%'), findsOneWidget);
    lateResponse!.complete(snapshot(75));
    await tester.pumpAndSettle();
    expect(find.text('91%'), findsOneWidget);
    expect(find.text('75%'), findsNothing);
  });
}
