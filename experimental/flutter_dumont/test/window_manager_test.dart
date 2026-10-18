import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/windows/dumont_window_manager.dart';

void main() {
  test('opening twice reuses a window and raises it to front', () {
    final manager = DumontWindowManager();
    addTearDown(manager.dispose);
    manager.open(id: 'linus', title: 'Linus Security');
    manager.open(id: 'notes', title: 'Notas');
    manager.open(id: 'linus', title: 'Linus Security');
    expect(manager.windows.map((w) => w.id).toList(), ['notes', 'linus']);
  });

  test('dragging and resizing remain within the available viewport', () {
    final manager = DumontWindowManager();
    addTearDown(manager.dispose);
    manager.open(id: 'linus', title: 'Linus Security');
    const viewport = Size(1000, 700);
    manager.moveBy('linus', const Offset(5000, 5000), viewport);
    final bottomRight = manager.geometryFor('linus', viewport);
    expect(bottomRight.right, lessThanOrEqualTo(viewport.width));
    expect(bottomRight.bottom, lessThanOrEqualTo(viewport.height));
    manager.resizeBy('linus', const Offset(5000, 5000), viewport);
    final resized = manager.geometryFor('linus', viewport);
    expect(resized.right, lessThanOrEqualTo(viewport.width));
    expect(resized.bottom, lessThanOrEqualTo(viewport.height));
    manager.close('linus');
    expect(manager.windows, isEmpty);
  });

  test('small viewport still contains the floating window', () {
    final manager = DumontWindowManager();
    addTearDown(manager.dispose);
    manager.open(id: 'linus', title: 'Linus Security');
    const mobile = Size(330, 280);
    final rect = manager.geometryFor('linus', mobile);
    expect(rect.left, greaterThanOrEqualTo(0));
    expect(rect.top, greaterThanOrEqualTo(0));
    expect(rect.right, lessThanOrEqualTo(mobile.width));
    expect(rect.bottom, lessThanOrEqualTo(mobile.height));
  });
}
