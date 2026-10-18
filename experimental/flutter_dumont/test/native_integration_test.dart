import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/main.dart';
import 'package:dumont_experience/services/android_bridge.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  const channel = MethodChannel('br.com.dumont.experience/native');

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test(
    'listApps accepts native PNG bytes without leaking invalid entries',
    () async {
      final pngBytes = Uint8List.fromList([137, 80, 78, 71]);
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (call) async {
            if (call.method == 'listApps') {
              return [
                {
                  'label': 'Meus Arquivos',
                  'packageName': 'com.samsung.myfiles',
                  'component': 'com.samsung.myfiles/.Main',
                  'iconPng': pngBytes,
                },
                {
                  'label': 'Outro',
                  'packageName': 'com.example.other',
                  'component': 'com.example.other/.Main',
                  'iconPng': 'not a png',
                },
              ];
            }
            throw PlatformException(code: 'unexpected');
          });
      final apps = await const DumontNativeBridge().listApps();
      expect(apps.length, 2);
      expect(apps.first.iconPng, pngBytes);
      expect(apps.last.iconPng, isNull);
    },
  );

  test(
    'battery, transport and device are decoded from native snapshot',
    () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (call) async {
            if (call.method == 'getDeviceStatus') {
              return {
                'batteryPercent': 73,
                'isCharging': false,
                'networkType': 'wifi',
                'model': 'SM-X510',
                'androidVersion': '16',
              };
            }
            throw PlatformException(code: 'unexpected');
          });
      final info = await const DumontNativeBridge().getDeviceStatus();
      expect(info.batteryPercent, 73);
      expect(info.isCharging, false);
      expect(info.networkType, 'wifi');
      expect(info.model, 'SM-X510');
      expect(info.androidVersion, '16');
    },
  );

  test(
    'invalid native status does not claim battery or connectivity',
    () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (call) async {
            if (call.method == 'getDeviceStatus') {
              return {
                'batteryPercent': 120,
                'networkType': 'invented',
                'model': '',
              };
            }
            throw PlatformException(code: 'unexpected');
          });
      final info = await const DumontNativeBridge().getDeviceStatus();
      expect(info.batteryPercent, isNull);
      expect(info.networkType, 'unknown');
      expect(info.model, 'Desconhecido');
    },
  );

  test('only whitelisted public settings can be opened', () async {
    String? called;
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async {
          called = (call.arguments as Map)['id'] as String;
          return true;
        });
    expect(await const DumontNativeBridge().openSettings('wifi'), true);
    expect(called, 'wifi');
    await expectLater(
      const DumontNativeBridge().openSettings('some_private_samsung_action'),
      throwsArgumentError,
    );
    expect(called, 'wifi');
  });

  testWidgets(
    'native device snapshot appears instead of synthetic full battery',
    (tester) async {
      tester.view.physicalSize = const Size(1000, 700);
      tester.view.devicePixelRatio = 1;
      addTearDown(tester.view.resetPhysicalSize);
      addTearDown(tester.view.resetDevicePixelRatio);
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (call) async {
            if (call.method == 'listApps') return <Map<String, Object?>>[];
            if (call.method == 'getDeviceStatus') {
              return {
                'batteryPercent': 61,
                'isCharging': true,
                'networkType': 'wifi',
                'model': 'SM-X510',
                'androidVersion': '16',
              };
            }
            return true;
          });
      await tester.pumpWidget(const DumontApp());
      await tester.pumpAndSettle();
      expect(find.text('61%'), findsOneWidget);
      expect(find.text('100%'), findsNothing);
    },
  );
}
