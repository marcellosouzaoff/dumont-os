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

  test('Android bridge decodes only valid launchable apps', () async {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async {
          if (call.method == 'listApps') {
            return [
              {
                'label': 'Chrome',
                'packageName': 'com.android.chrome',
                'component':
                    'com.android.chrome/com.google.android.apps.chrome.Main',
              },
              {'label': '', 'packageName': '', 'component': ''},
            ];
          }
          throw PlatformException(code: 'unknown');
        });
    final apps = await DumontNativeBridge().listApps();
    expect(apps.length, 1);
    expect(apps.single.label, 'Chrome');
    expect(
      apps.single.component,
      'com.android.chrome/com.google.android.apps.chrome.Main',
    );
  });

  test('bridge explicitly reports missing native channel', () async {
    await expectLater(
      DumontNativeBridge().listApps(),
      throwsA(isA<MissingPluginException>()),
    );
  });

  testWidgets(
    'native catalog displays real apps and launches selected component',
    (tester) async {
      tester.view.physicalSize = const Size(1000, 700);
      tester.view.devicePixelRatio = 1;
      addTearDown(tester.view.resetPhysicalSize);
      addTearDown(tester.view.resetDevicePixelRatio);

      String? launched;
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (call) async {
            if (call.method == 'listApps') {
              return [
                {
                  'label': 'Aplicativo Real',
                  'packageName': 'br.com.exemplo',
                  'component': 'br.com.exemplo/.Principal',
                },
              ];
            }
            if (call.method == 'launchApp') {
              launched = (call.arguments as Map)['component'] as String;
              return true;
            }
            throw PlatformException(code: 'unknown');
          });

      await tester.pumpWidget(const DumontApp());
      await tester.pumpAndSettle();
      expect(find.text('Aplicativo Real'), findsOneWidget);
      expect(find.text('Navegador'), findsNothing);
      await tester.tap(find.text('Aplicativo Real'));
      await tester.pumpAndSettle();
      expect(launched, 'br.com.exemplo/.Principal');
    },
  );
}
