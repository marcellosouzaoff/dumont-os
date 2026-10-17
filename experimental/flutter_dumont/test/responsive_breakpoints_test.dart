import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/main.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  Future<void> resize(WidgetTester tester, double width, double height) async {
    tester.view.physicalSize = Size(width, height);
    tester.view.devicePixelRatio = 1;
    await tester.pump();
    await tester.pumpAndSettle();
    // Rendering overflow is a correctness failure, not just a warning.
    expect(tester.takeException(), isNull,
        reason: 'Overflow or layout exception at ${width}x$height');
  }

  testWidgets('DUMONT remains usable at phone, tablet and desktop sizes',
      (tester) async {
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    await resize(tester, 390, 844);
    await tester.pumpWidget(const DumontApp());
    await tester.pumpAndSettle();
    expect(tester.takeException(), isNull);
    expect(find.byKey(const Key('app-window')), findsOneWidget);
    expect(find.byKey(const Key('dumont-dock')), findsOneWidget);

    for (final size in <Size>[
      const Size(320, 568),
      const Size(360, 640),
      const Size(412, 915), // Galaxy A55 logical-size class
      const Size(600, 960),
      const Size(600, 360), // short split-screen/multi-window
      const Size(800, 480),
      const Size(690, 700),
      const Size(800, 1280),
      const Size(1024, 600),
      const Size(1279, 720),
      const Size(1280, 800),
      const Size(1920, 1080), // desktop/DeX viewport simulation
    ]) {
      await resize(tester, size.width, size.height);
      expect(find.byKey(const Key('app-window')), findsOneWidget);
      expect(find.byKey(const Key('dumont-dock')), findsOneWidget);
    }
  });

  testWidgets('small phone can open and close Ghost with no overflow',
      (tester) async {
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    tester.view.physicalSize = const Size(320, 568);
    tester.view.devicePixelRatio = 1;
    await tester.pumpWidget(const DumontApp());
    await tester.pumpAndSettle();
    expect(tester.takeException(), isNull);

    await tester.tap(find.byKey(const Key('ghost-edge')));
    await tester.pumpAndSettle();
    expect(tester.takeException(), isNull);
    expect(find.byKey(const Key('ghost-panel')), findsOneWidget);
    await tester.tap(find.byKey(const Key('ghost-close')));
    await tester.pumpAndSettle();
    expect(tester.takeException(), isNull);
    expect(find.byKey(const Key('ghost-panel')), findsNothing);
  });
}
