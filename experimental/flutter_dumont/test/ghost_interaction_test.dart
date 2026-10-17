import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/main.dart';

void main() {
  testWidgets('ghost opens after smooth swipe with small deltas', (
    tester,
  ) async {
    tester.view.physicalSize = const Size(1152, 720);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    await tester.pumpWidget(const DumontApp());
    expect(find.byKey(const Key('ghost-panel')), findsNothing);
    final swipe = await tester.startGesture(const Offset(1110, 360));
    for (var i = 0; i < 16; i++) {
      await swipe.moveBy(const Offset(-6, 0));
      await tester.pump(const Duration(milliseconds: 20));
    }
    await swipe.up();
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('ghost-panel')), findsOneWidget);
  });

  testWidgets(
    'ghost rail supports accessible tap without Android edge gesture',
    (tester) async {
      tester.view.physicalSize = const Size(1152, 720);
      tester.view.devicePixelRatio = 1;
      addTearDown(tester.view.resetPhysicalSize);
      addTearDown(tester.view.resetDevicePixelRatio);
      await tester.pumpWidget(const DumontApp());
      await tester.tap(find.byKey(const Key('ghost-edge')));
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('ghost-panel')), findsOneWidget);
      await tester.tap(find.byKey(const Key('ghost-close')));
      await tester.pumpAndSettle();
      expect(find.byKey(const Key('ghost-panel')), findsNothing);
    },
  );
}
