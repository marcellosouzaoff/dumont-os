import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/windows/dumont_window_manager.dart';

void main() {
  test('maximize remembers geometry and follows viewport size changes', () {
    final manager = DumontWindowManager();
    addTearDown(manager.dispose);
    manager.open(id: 'linus', title: 'Linus Security');
    const desktop = Size(1100, 700);
    manager.moveBy('linus', const Offset(120, 70), desktop);
    final original = manager.geometryFor('linus', desktop);

    manager.toggleMaximize('linus');
    expect(manager.isMaximized('linus'), isTrue);
    expect(
      manager.geometryFor('linus', desktop),
      const Rect.fromLTWH(8, 8, 1084, 684),
    );
    const tabletPortrait = Size(620, 960);
    expect(
      manager.geometryFor('linus', tabletPortrait),
      const Rect.fromLTWH(8, 8, 604, 944),
    );

    // Dragging a maximized window must not secretly move its restore rect.
    manager.moveBy('linus', const Offset(500, 200), tabletPortrait);
    manager.resizeBy('linus', const Offset(-200, -120), tabletPortrait);
    expect(
      manager.geometryFor('linus', tabletPortrait),
      const Rect.fromLTWH(8, 8, 604, 944),
    );
    manager.toggleMaximize('linus');
    expect(manager.isMaximized('linus'), isFalse);
    expect(manager.geometryFor('linus', desktop), original);

    manager.toggleMaximize('linus');
    manager.close('linus');
    manager.open(id: 'linus', title: 'Linus Security');
    expect(manager.isMaximized('linus'), isFalse);
  });

  testWidgets('maximize action adapts window to portrait and restores',
      (tester) async {
    tester.view.physicalSize = const Size(1100, 700);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    final manager = DumontWindowManager();
    addTearDown(manager.dispose);
    manager.open(id: 'linus', title: 'Linus Security');
    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: DumontWindowLayer(
            manager: manager,
            contentBuilder: (_) => const Text('Janela DUMONT'),
          ),
        ),
      ),
    );
    final window = find.byKey(const Key('window-linus'));
    final normalSize = tester.getSize(window);
    await tester.tap(find.byKey(const Key('window-maximize-linus')));
    await tester.pumpAndSettle();
    expect(manager.isMaximized('linus'), isTrue);
    expect(tester.getSize(window).width, closeTo(1084, 0.1));
    expect(tester.getSize(window).height, closeTo(684, 0.1));

    tester.view.physicalSize = const Size(620, 960);
    await tester.pumpAndSettle();
    expect(tester.getSize(window).width, closeTo(604, 0.1));
    expect(tester.getSize(window).height, closeTo(944, 0.1));

    await tester.tap(find.byKey(const Key('window-maximize-linus')));
    await tester.pumpAndSettle();
    expect(manager.isMaximized('linus'), isFalse);
    expect(tester.getSize(window).width, closeTo(normalSize.width, 0.1));
  });
}
