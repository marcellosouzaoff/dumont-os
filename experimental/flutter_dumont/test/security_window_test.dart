import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/main.dart';
import 'package:dumont_experience/theme/dumont_theme.dart';
import 'package:dumont_experience/widgets/dumont_state_badge.dart';

void main() {
  test('Paleta 01 reproduces approved design tokens', () {
    expect(DumontTheme.baritoneBlue, const Color(0xFF272836));
    expect(DumontTheme.cloudDancer, const Color(0xFFF0EEE9));
    expect(DumontTheme.curdsAndWhey, const Color(0xFFBCA483));
    expect(DumontTheme.stateUnknown, const Color(0xFF626474));
  });

  testWidgets('status component starts UNKNOWN instead of success', (tester) async {
    await tester.pumpWidget(const MaterialApp(
      home: Scaffold(body: DumontStateBadge(label: 'Knox', state: DumontState.unknown)),
    ));
    expect(find.text('Knox · UNKNOWN'), findsOneWidget);
    expect(find.byIcon(Icons.help_outline_rounded), findsOneWidget);
    expect(find.byIcon(Icons.check_circle_outline_rounded), findsNothing);
  });

  testWidgets('settings opens a closable Linus Security floating window', (tester) async {
    tester.view.physicalSize = const Size(1000, 700);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    await tester.pumpWidget(const DumontApp());
    await tester.pump();
    expect(find.byKey(const Key('window-linus')), findsNothing);
    await tester.tap(find.byKey(const Key('dock-5')));
    await tester.pump();
    expect(find.byKey(const Key('window-linus')), findsOneWidget);
    expect(find.text('Knox · UNKNOWN'), findsOneWidget);
    await tester.tap(find.byKey(const Key('window-close-linus')));
    await tester.pump();
    expect(find.byKey(const Key('window-linus')), findsNothing);
  });
}
