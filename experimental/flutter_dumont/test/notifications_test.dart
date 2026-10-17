import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/main.dart';
import 'package:dumont_experience/services/android_bridge.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  const channel = MethodChannel('br.com.dumont.experience/native');
  tearDown(
    () => TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null),
  );

  test('listener denied means no notifications or false claims', () async {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async {
          if (call.method == 'getNotificationSnapshot') {
            return {
              'accessGranted': false,
              'connected': false,
              'notifications': [
                {
                  'packageName': 'com.secret',
                  'appLabel': 'Private',
                  'postTimeMs': 1000,
                },
              ],
            };
          }
          return null;
        });
    final status = await const DumontNativeBridge().getNotificationSnapshot();
    expect(status.accessGranted, false);
    expect(status.notifications, isEmpty);
  });

  test(
    'granted snapshot only decodes app and timestamp, no message text',
    () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (call) async {
            if (call.method == 'getNotificationSnapshot') {
              return {
                'accessGranted': true,
                'connected': true,
                'notifications': [
                  {
                    'packageName': 'com.example.chat',
                    'appLabel': 'Chat',
                    'postTimeMs': 1760000000000,
                    'title': 'SENSITIVE',
                    'text': 'PRIVATE MESSAGE',
                  },
                  {'packageName': '', 'appLabel': '', 'postTimeMs': -10},
                ],
              };
            }
            return null;
          });
      final status = await const DumontNativeBridge().getNotificationSnapshot();
      expect(status.accessGranted, true);
      expect(status.connected, true);
      expect(status.notifications.length, 1);
      expect(status.notifications.single.appLabel, 'Chat');
      expect(status.notifications.single.packageName, 'com.example.chat');
      expect(status.notifications.single.postTimeMs, 1760000000000);
      expect(
        status.notifications.single.toString(),
        isNot(contains('PRIVATE')),
      );
    },
  );

  test(
    'opening OS notification access requires an explicit bridge call',
    () async {
      String? called;
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (call) async {
            called = call.method;
            return true;
          });
      expect(called, isNull);
      expect(
        await const DumontNativeBridge().openNotificationAccessSettings(),
        true,
      );
      expect(called, 'openNotificationAccessSettings');
    },
  );

  testWidgets('Ghost shows permission request and no data before grant', (
    tester,
  ) async {
    tester.view.physicalSize = const Size(1000, 700);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async {
          if (call.method == 'listApps') return <Map<String, Object?>>[];
          if (call.method == 'getDeviceStatus') return {};
          if (call.method == 'getNotificationSnapshot') {
            return {
              'accessGranted': false,
              'connected': false,
              'notifications': <Object>[],
            };
          }
          return true;
        });
    await tester.pumpWidget(const DumontApp(ghostInitiallyOpen: true));
    await tester.pumpAndSettle();
    expect(find.textContaining('Acesso não concedido'), findsWidgets);
    expect(find.textContaining('PRIVATE MESSAGE'), findsNothing);
  });
}
