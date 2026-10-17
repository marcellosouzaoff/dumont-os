import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dumont_experience/main.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  setUpAll(() async {
    await (FontLoader(
      'DM Sans',
    )..addFont(rootBundle.load('assets/fonts/DMSans.ttf'))).load();
    await (FontLoader(
      'Cormorant Garamond',
    )..addFont(rootBundle.load('assets/fonts/CormorantGaramond.ttf'))).load();
    await (FontLoader('MaterialIcons')
          ..addFont(rootBundle.load('assets/fonts/MaterialIcons-Regular.otf')))
        .load();
  });

  testWidgets('DUMONT desktop Flutter render', (tester) async {
    tester.view.physicalSize = const Size(1440, 900);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);

    await tester.pumpWidget(const DumontApp());
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('app-window')), findsOneWidget);
    expect(find.byKey(const Key('dumont-dock')), findsOneWidget);
    expect(find.byKey(const Key('ghost-panel')), findsNothing);
    await expectLater(
      find.byType(DumontDesktop),
      matchesGoldenFile('goldens/desktop.png'),
    );
  });

  testWidgets('DUMONT tablet Flutter render', (tester) async {
    tester.view.physicalSize = const Size(1000, 700);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);

    await tester.pumpWidget(const DumontApp());
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('app-window')), findsOneWidget);
    await expectLater(
      find.byType(DumontDesktop),
      matchesGoldenFile('goldens/tablet.png'),
    );
  });

  testWidgets('DUMONT mobile Flutter render', (tester) async {
    tester.view.physicalSize = const Size(390, 844);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    await tester.pumpWidget(const DumontApp());
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('app-window')), findsOneWidget);
    await expectLater(
      find.byType(DumontDesktop),
      matchesGoldenFile('goldens/mobile.png'),
    );
  });

  testWidgets('Ghost stays hidden and can be opened', (tester) async {
    tester.view.physicalSize = const Size(1440, 900);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    await tester.pumpWidget(const DumontApp());
    expect(find.byKey(const Key('ghost-panel')), findsNothing);
    await tester.dragFrom(const Offset(1434, 450), const Offset(-150, 0));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('ghost-panel')), findsOneWidget);
    await expectLater(
      find.byType(DumontDesktop),
      matchesGoldenFile('goldens/ghost.png'),
    );
    await tester.tap(find.byKey(const Key('ghost-close')));
    await tester.pumpAndSettle();
    expect(find.byKey(const Key('ghost-panel')), findsNothing);
  });

  testWidgets('Desktop category filter and search', (tester) async {
    tester.view.physicalSize = const Size(1440, 900);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.resetPhysicalSize);
    addTearDown(tester.view.resetDevicePixelRatio);
    await tester.pumpWidget(const DumontApp());
    await tester.tap(find.byKey(const Key('category-Recentes')));
    await tester.pumpAndSettle();
    expect(find.text('5 itens'), findsOneWidget);
    await tester.enterText(find.byKey(const Key('search-input')), 'Navegador');
    await tester.pumpAndSettle();
    expect(find.text('1 itens'), findsOneWidget);
  });
}
