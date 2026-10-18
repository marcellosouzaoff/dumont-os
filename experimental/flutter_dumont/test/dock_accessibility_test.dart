import 'package:dumont_experience/main.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  const dockActions = [
    'Aplicativos',
    'Arquivos',
    'Navegador',
    'Música',
    'Mensagens',
    'Ajustes',
  ];

  for (final viewport in [const Size(390, 844), const Size(1152, 720)]) {
    testWidgets('dock exposes all six accessible actions at $viewport', (
      tester,
    ) async {
      tester.view.physicalSize = viewport;
      tester.view.devicePixelRatio = 1;
      addTearDown(tester.view.resetPhysicalSize);
      addTearDown(tester.view.resetDevicePixelRatio);

      await tester.pumpWidget(const DumontApp());

      for (final action in dockActions) {
        expect(
          find.byTooltip(action),
          findsOneWidget,
          reason: 'Dock action "$action" needs an accessible tooltip',
        );
      }
    });
  }
}
