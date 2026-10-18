import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/main.dart';
import 'package:dumont_experience/services/android_bridge.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  const methodChannel = MethodChannel('br.com.dumont.experience/native');
  const eventControl = MethodChannel('br.com.dumont.experience/device_events');
  const eventName = 'br.com.dumont.experience/device_events';
  final codec = const StandardMethodCodec();
  var listenCalls = 0;
  var cancelCalls = 0;

  setUp(() {
    listenCalls = 0;
    cancelCalls = 0;
    final messenger =
        TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger;
    messenger.setMockMethodCallHandler(eventControl, (call) async {
      if (call.method == 'listen') listenCalls++;
      if (call.method == 'cancel') cancelCalls++;
      return null;
    });
    messenger.setMockMethodCallHandler(methodChannel, (call) async {
      if (call.method == 'getDeviceStatus') {
        return {
          'batteryPercent': 55,
          'isCharging': false,
          'networkType': 'wifi',
          'model': 'SM-X510',
          'androidVersion': '16',
        };
      }
      if (call.method == 'listApps') return <Object>[];
      if (call.method == 'getNotificationSnapshot') {
        return {
          'accessGranted': false,
          'connected': false,
          'notifications': <Object>[],
        };
      }
      return null;
    });
  });

  tearDown(() {
    final messenger =
        TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger;
    messenger.setMockMethodCallHandler(methodChannel, null);
    messenger.setMockMethodCallHandler(eventControl, null);
  });

  test(
    'Dart EventChannel emits live state and cancels native listener',
    () async {
      final received = <DumontDeviceInfo>[];
      final sub = const DumontNativeBridge().deviceStatusEvents().listen(
        received.add,
      );
      await Future<void>.delayed(Duration.zero);
      expect(listenCalls, 1);

      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .handlePlatformMessage(
            eventName,
            codec.encodeSuccessEnvelope({
              'batteryPercent': 82,
              'isCharging': true,
              'networkType': 'cellular',
              'model': 'SM-X510',
              'androidVersion': '16',
            }),
            (_) {},
          );
      await Future<void>.delayed(Duration.zero);
      expect(received.single.batteryPercent, 82);
      expect(received.single.networkType, 'cellular');

      await sub.cancel();
      expect(cancelCalls, 1);
    },
  );

  testWidgets('live event changes visible battery without rebuilding app', (
    tester,
  ) async {
    tester.view.physicalSize = const Size(1152, 720);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);

    await tester.pumpWidget(
      DumontApp(
        deviceStatusEvents: const DumontNativeBridge().deviceStatusEvents(),
      ),
    );
    await tester.pumpAndSettle();
    expect(find.text('55%'), findsOneWidget);
    expect(listenCalls, 1);
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .handlePlatformMessage(
          eventName,
          codec.encodeSuccessEnvelope({
            'batteryPercent': 79,
            'isCharging': true,
            'networkType': 'ethernet',
            'model': 'SM-X510',
            'androidVersion': '16',
          }),
          (_) {},
        );
    await tester.pumpAndSettle();
    expect(find.text('79%'), findsOneWidget);
    expect(find.text('55%'), findsNothing);

    await tester.pumpWidget(const SizedBox());
    await tester.pumpAndSettle();
    expect(cancelCalls, 1);
  });
}
