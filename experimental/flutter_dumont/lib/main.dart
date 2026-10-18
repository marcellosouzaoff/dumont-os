import 'dart:async';
import 'dart:io' show Platform;

import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'theme/dumont_theme.dart';
import 'services/android_bridge.dart';
import 'widgets/dumont_state_badge.dart';
import 'windows/dumont_window_manager.dart';

void main() => runApp(const DumontApp());

abstract final class Brand {
  static const dark = DumontTheme.baritoneBlue;
  static const cream = DumontTheme.cloudDancer;
  static const accent = DumontTheme.curdsAndWhey;
  static const ink = DumontTheme.ink;
  static const muted = DumontTheme.muted;
}

class DumontApp extends StatelessWidget {
  const DumontApp({
    super.key,
    this.ghostInitiallyOpen = false,
    this.deviceStatusEvents,
  });
  final bool ghostInitiallyOpen;

  /// Optional stream enables deterministic tests without an Android host.
  final Stream<DumontDeviceInfo>? deviceStatusEvents;
  @override
  Widget build(BuildContext context) => MaterialApp(
    debugShowCheckedModeBanner: false,
    title: 'DUMONT Flutter Experience',
    theme: DumontTheme.darkTheme,
    home: DumontDesktop(
      ghostInitiallyOpen: ghostInitiallyOpen,
      deviceStatusEvents: deviceStatusEvents,
    ),
  );
}

class DemoApp {
  const DemoApp(
    this.name,
    this.icon,
    this.tint,
    this.group, {
    this.component,
    this.iconPng,
  });

  final String name;
  final IconData icon;
  final Color tint;
  final String group;
  final String? component;
  final Uint8List? iconPng;
}

const demoApps = <DemoApp>[
  DemoApp('Arquivos', Icons.folder_rounded, Color(0xFFBD956C), 'Sistema'),
  DemoApp('Navegador', Icons.public_rounded, Color(0xFF617D99), 'Recentes'),
  DemoApp('Música', Icons.music_note_rounded, Color(0xFFC77E77), 'Recentes'),
  DemoApp('Galeria', Icons.photo_library_rounded, Color(0xFF898DC2), 'Samsung'),
  DemoApp(
    'Calendário',
    Icons.calendar_month_rounded,
    Color(0xFF72988B),
    'Recentes',
  ),
  DemoApp('Câmera', Icons.camera_alt_rounded, Color(0xFF888C9A), 'Sistema'),
  DemoApp('Notas', Icons.edit_note_rounded, Color(0xFFC4AA73), 'Recentes'),
  DemoApp(
    'Mensagens',
    Icons.chat_bubble_rounded,
    Color(0xFF7C9A94),
    'Recentes',
  ),
  DemoApp('Terminal', Icons.terminal_rounded, Color(0xFF505A73), 'Sistema'),
  DemoApp('Ajustes', Icons.settings_rounded, Color(0xFF8D8790), 'Sistema'),
  DemoApp('Mapas', Icons.map_rounded, Color(0xFF95A67B), 'Google'),
  DemoApp('E-mail', Icons.mail_rounded, Color(0xFF86A0B8), 'Google'),
];
const categories = ['Todos', 'Recentes', 'Samsung', 'Google', 'Sistema'];

class DumontDesktop extends StatefulWidget {
  const DumontDesktop({
    super.key,
    this.ghostInitiallyOpen = false,
    this.deviceStatusEvents,
  });
  final bool ghostInitiallyOpen;
  final Stream<DumontDeviceInfo>? deviceStatusEvents;
  @override
  State<DumontDesktop> createState() => _DumontDesktopState();
}

class _DumontDesktopState extends State<DumontDesktop>
    with WidgetsBindingObserver {
  final DumontWindowManager _windows = DumontWindowManager();
  final DumontNativeBridge _nativeBridge = const DumontNativeBridge();
  DumontDeviceInfo? _deviceInfo;
  StreamSubscription<DumontDeviceInfo>? _deviceEventsSubscription;
  // Event stream wins over older MethodChannel responses.
  int _deviceEventRevision = 0;
  int _deviceRequestRevision = 0;
  DumontNotificationSnapshot? _notificationSnapshot;
  List<DemoApp>? _realApps;
  List<String> get _categories =>
      _realApps == null ? categories : const ['Todos', 'Instalados'];
  List<DemoApp> get _catalog => _realApps ?? demoApps;
  bool get _nativeAvailable => _realApps != null;
  bool browserOpen = true, ghostOpen = false;
  double _ghostDragTravel = 0;
  String category = 'Todos', search = '', notice = '';
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    ghostOpen = widget.ghostInitiallyOpen;
    _refreshNativeApps();
    _refreshDeviceStatus();
    _listenToDeviceStatus();
    _refreshNotifications();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _deviceEventsSubscription?.cancel();
    _windows.dispose();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state != AppLifecycleState.resumed) return;
    // Android settings can change while DUMONT is in the background.
    // Refresh on return without polling, including consent state.
    _refreshDeviceStatus(force: true);
    _refreshNotifications();
  }

  void _openGhost() {
    if (!mounted || ghostOpen) return;
    setState(() {
      ghostOpen = true;
      _ghostDragTravel = 0;
    });
    // Read-only status refresh; never grants notification privileges.
    _refreshNotifications();
  }

  void _openLinus() => _windows.open(id: 'linus', title: 'Linus Security');

  Widget _windowContents(DumontWindow window) {
    if (window.id != 'linus') return const SizedBox.shrink();
    return Padding(
      padding: const EdgeInsets.all(18),
      child: ListView(
        children: const [
          Text(
            'Linus Security',
            style: TextStyle(
              fontSize: 22,
              fontWeight: FontWeight.w600,
              color: DumontTheme.baritoneBlue,
            ),
          ),
          SizedBox(height: 13),
          DumontStateBadge(label: 'Knox', state: DumontState.unknown),
          SizedBox(height: 13),
          Text(
            'Integração com Knox não verificada. Este painel não altera '
            'configurações de segurança, nem solicita permissões.',
            style: TextStyle(color: DumontTheme.ink, fontSize: 12, height: 1.4),
          ),
        ],
      ),
    );
  }

  Future<void> _refreshNativeApps() async {
    try {
      final installed = await _nativeBridge.listApps();
      if (!mounted) return;
      setState(() {
        _realApps = installed
            .map(
              (app) => DemoApp(
                app.label,
                Icons.apps_rounded,
                Brand.ink,
                'Instalados',
                component: app.component,
                iconPng: app.iconPng,
              ),
            )
            .toList(growable: false);
        category = 'Todos';
      });
    } on MissingPluginException {
      // Web, tests and unsupported hosts remain visibly demonstrative.
    } on PlatformException {
      // Native call failed: never describe the sample data as installed apps.
      if (mounted) notify('Catálogo Android indisponível: prévia visual');
    }
  }

  void _listenToDeviceStatus() {
    // Never attempt Android-only EventChannel registration on web/desktop or
    // Flutter tests unless a stream is explicitly supplied.
    final events =
        widget.deviceStatusEvents ??
        ((!kIsWeb && Platform.isAndroid)
            ? _nativeBridge.deviceStatusEvents()
            : null);
    if (events == null) return;
    _deviceEventsSubscription = events.listen(
      (snapshot) {
        ++_deviceEventRevision;
        if (mounted) setState(() => _deviceInfo = snapshot);
      },
      onError: (Object _) {
        // Unsupported hosts or transient native errors keep the last
        // verified snapshot; the MethodChannel fallback remains available.
      },
    );
  }

  Future<void> _refreshDeviceStatus({bool force = false}) async {
    final eventRevisionAtStart = _deviceEventRevision;
    final requestRevision = ++_deviceRequestRevision;
    try {
      final snapshot = await _nativeBridge.getDeviceStatus();
      // Do not allow a stale one-shot call (or concurrent older request)
      // to overwrite a newer EventChannel reading.
      if (!mounted ||
          _deviceEventRevision != eventRevisionAtStart ||
          _deviceRequestRevision != requestRevision) {
        return;
      }
      if (force || _deviceInfo == null) {
        setState(() => _deviceInfo = snapshot);
      }
    } on MissingPluginException {
      // No native snapshot outside supported Android; leave UNKNOWN.
    } on PlatformException {
      if (mounted) notify('Estado do dispositivo indisponível');
    }
  }

  Future<void> _openPublicSettings(String id) async {
    try {
      final ok = await _nativeBridge.openSettings(id);
      if (!ok && mounted) notify('Configuração Android indisponível');
    } on MissingPluginException {
      if (mounted) notify('Configuração Android indisponível na prévia web');
    } on PlatformException {
      if (mounted) notify('Não foi possível abrir a configuração');
    } on ArgumentError {
      if (mounted) notify('Ação de configuração não permitida');
    }
  }

  Future<void> _refreshNotifications() async {
    try {
      final snapshot = await _nativeBridge.getNotificationSnapshot();
      if (mounted) setState(() => _notificationSnapshot = snapshot);
    } on MissingPluginException {
      // Web and tests: do not claim permission or show any private data.
    } on PlatformException {
      if (mounted) setState(() => _notificationSnapshot = null);
    }
  }

  /// The user must tap to open Android's special-access grant screen.
  Future<void> _handleNotificationTap() async {
    if (_notificationSnapshot?.accessGranted == true) {
      await _refreshNotifications();
      return;
    }
    try {
      final opened = await _nativeBridge.openNotificationAccessSettings();
      if (!opened && mounted) notify('Configurações de acesso indisponíveis');
    } on MissingPluginException {
      if (mounted) notify('Permissão disponível somente no Android');
    } on PlatformException {
      if (mounted) notify('Não foi possível abrir permissões Android');
    }
  }

  Future<void> _launchApp(DemoApp app) async {
    final component = app.component;
    if (component == null) {
      notify('${app.name}: demonstração visual');
      return;
    }
    try {
      final launched = await _nativeBridge.launchApp(component);
      if (!launched && mounted) {
        notify('Não foi possível abrir ${app.name}');
      }
    } on PlatformException {
      if (mounted) notify('Falha ao abrir ${app.name} pelo Android');
    } on MissingPluginException {
      if (mounted) notify('Ponte Android indisponível');
    }
  }

  void notify(String value) => setState(() => notice = value);
  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.sizeOf(context).width;
    final compact = width < 690;
    final wide = width >= 1280;
    return Scaffold(
      body: Stack(
        children: [
          const Positioned.fill(
            child: CustomPaint(painter: WallpaperPainter()),
          ),
          SafeArea(
            child: Padding(
              padding: EdgeInsets.fromLTRB(
                compact ? 9 : 22,
                9,
                compact ? 9 : 22,
                0,
              ),
              child: Column(
                children: [
                  _header(width < 1120),
                  Expanded(
                    child: Stack(
                      children: [
                        if (wide && !browserOpen)
                          Positioned(
                            left: 12,
                            top: 34,
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: const [
                                Text(
                                  'SEU ESPAÇO DIGITAL',
                                  style: TextStyle(
                                    color: Brand.cream,
                                    fontSize: 10,
                                    letterSpacing: 2,
                                  ),
                                ),
                                SizedBox(height: 13),
                                Text(
                                  'Menos ruído.\nMais essência.',
                                  style: TextStyle(
                                    color: Brand.cream,
                                    fontFamily: 'Cormorant Garamond',
                                    fontSize: 26,
                                    height: 1.1,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        if (browserOpen)
                          Center(
                            child: Padding(
                              padding: EdgeInsets.fromLTRB(
                                wide ? 85 : 0,
                                compact ? 14 : 39,
                                wide ? 85 : 0,
                                compact ? 11 : 27,
                              ),
                              child: ConstrainedBox(
                                constraints: const BoxConstraints(
                                  maxWidth: 1040,
                                ),
                                child: _browser(compact),
                              ),
                            ),
                          ),
                        if (!browserOpen)
                          Center(
                            child: TextButton.icon(
                              onPressed: () =>
                                  setState(() => browserOpen = true),
                              icon: const Icon(Icons.apps_rounded),
                              label: const Text('Abrir aplicativos'),
                              style: TextButton.styleFrom(
                                foregroundColor: Brand.cream,
                              ),
                            ),
                          ),
                        Positioned.fill(
                          child: DumontWindowLayer(
                            manager: _windows,
                            contentBuilder: _windowContents,
                          ),
                        ),
                      ],
                    ),
                  ),
                  _dock(compact),
                  const SizedBox(height: 10),
                ],
              ),
            ),
          ),
          if (!ghostOpen)
            Positioned(
              top: 75,
              bottom: 100,
              // Keep the touch area away from Android's right-edge
              // navigation gesture. The compact view also exposes an
              // explicit accessible button as a gesture-free fallback.
              right: compact ? 20 : 0,
              width: compact ? 42 : 62,
              child: Semantics(
                label: 'Abrir painel Ghost',
                button: true,
                child: GestureDetector(
                  key: const Key('ghost-edge'),
                  behavior: HitTestBehavior.translucent,
                  onTap: _openGhost,
                  onHorizontalDragStart: (_) => _ghostDragTravel = 0,
                  onHorizontalDragUpdate: (details) {
                    _ghostDragTravel += details.primaryDelta ?? 0;
                    // Smooth swipes arrive in many small move events.
                    if (_ghostDragTravel <= -48) _openGhost();
                  },
                  onHorizontalDragEnd: (_) {
                    if (_ghostDragTravel <= -48) _openGhost();
                  },
                ),
              ),
            ),
          if (!ghostOpen && compact)
            Positioned(
              top: 88,
              right: 24,
              child: Semantics(
                label: 'Abrir Ghost por botão',
                button: true,
                child: IconButton(
                  key: const Key('ghost-open-button'),
                  tooltip: 'Abrir Ghost',
                  icon: const Icon(Icons.tune_rounded, size: 20),
                  color: Brand.cream,
                  style: IconButton.styleFrom(
                    backgroundColor: Brand.dark,
                    minimumSize: const Size(44, 44),
                  ),
                  onPressed: _openGhost,
                ),
              ),
            ),
          if (ghostOpen) ...[
            Positioned.fill(
              child: GestureDetector(
                key: const Key('ghost-scrim'),
                onTap: () => setState(() => ghostOpen = false),
                child: Container(color: Colors.black.withValues(alpha: .25)),
              ),
            ),
            Positioned(
              top: compact ? 73 : 85,
              bottom: 100,
              right: compact ? 8 : 20,
              width: compact ? width * .83 : 338,
              child: _ghost(),
            ),
          ],
          if (notice.isNotEmpty)
            Positioned(
              left: 18,
              right: 18,
              bottom: compact ? 96 : 110,
              child: Center(
                child: Material(
                  color: Brand.dark,
                  elevation: 10,
                  borderRadius: BorderRadius.circular(12),
                  child: Padding(
                    padding: const EdgeInsets.all(11),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const Icon(
                          Icons.info_outline,
                          color: Brand.cream,
                          size: 18,
                        ),
                        const SizedBox(width: 9),
                        Flexible(
                          child: Text(
                            notice,
                            style: const TextStyle(
                              color: Brand.cream,
                              fontSize: 12,
                            ),
                          ),
                        ),
                        IconButton(
                          onPressed: () => notify(''),
                          icon: const Icon(
                            Icons.close,
                            color: Brand.cream,
                            size: 16,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _header(bool compact) => Container(
    height: 57,
    padding: EdgeInsets.symmetric(horizontal: compact ? 9 : 18),
    decoration: BoxDecoration(
      color: Brand.dark.withValues(alpha: .97),
      border: Border.all(color: Brand.cream.withValues(alpha: .14)),
      borderRadius: BorderRadius.circular(13),
    ),
    child: Row(
      children: [
        Text(
          'DUMONT',
          style: TextStyle(
            letterSpacing: compact ? 2.4 : 3.9,
            fontWeight: FontWeight.w600,
            fontSize: compact ? 13 : 17,
            color: Brand.cream,
          ),
        ),
        SizedBox(width: compact ? 8 : 29),
        if (!compact) ...[
          _menu('Aplicativos', () => setState(() => browserOpen = true)),
          _menu('Configurações', _openLinus),
          _menu('Terminal', () => notify('Prévia: integração Kotlin pendente')),
        ] else
          IconButton(
            key: const Key('apps-button'),
            onPressed: () => setState(() => browserOpen = !browserOpen),
            icon: const Icon(Icons.apps_rounded, color: Brand.cream),
          ),
        const Spacer(),
        if (!compact)
          const Text(
            'EXPERIÊNCIA FLUTTER · PRÉVIA',
            style: TextStyle(
              color: Brand.muted,
              fontSize: 10,
              letterSpacing: 1,
            ),
          ),
        if (!compact) const SizedBox(width: 25),
        Icon(
          _deviceInfo?.networkType == 'wifi'
              ? Icons.wifi_rounded
              : _deviceInfo?.networkType == 'offline'
              ? Icons.wifi_off_rounded
              : Icons.network_check_rounded,
          size: 17,
          color: _deviceInfo == null ? Brand.muted : Brand.cream,
        ),
        const SizedBox(width: 12),
        Icon(
          _deviceInfo?.isCharging == true
              ? Icons.battery_charging_full_rounded
              : Icons.battery_std_rounded,
          size: 17,
          color: _deviceInfo == null ? Brand.muted : Brand.cream,
        ),
        const SizedBox(width: 5),
        Text(
          _deviceInfo?.batteryPercent == null
              ? '—'
              : '${_deviceInfo!.batteryPercent}%',
          style: const TextStyle(fontSize: 12, color: Brand.cream),
        ),
      ],
    ),
  );
  Widget _menu(String label, VoidCallback action) => TextButton(
    onPressed: action,
    style: TextButton.styleFrom(foregroundColor: Brand.cream),
    child: Text(label, style: const TextStyle(fontSize: 12)),
  );

  Widget _browser(bool compact) {
    final visible = _catalog
        .where(
          (app) =>
              app.name.toLowerCase().contains(search.toLowerCase()) &&
              (category == 'Todos' || category == app.group),
        )
        .toList();
    return Container(
      key: const Key('app-window'),
      clipBehavior: Clip.antiAlias,
      decoration: BoxDecoration(
        color: Brand.cream,
        borderRadius: BorderRadius.circular(20),
        boxShadow: const [
          BoxShadow(
            color: Color(0x66000000),
            blurRadius: 33,
            offset: Offset(0, 17),
          ),
        ],
      ),
      child: Column(
        children: [
          SizedBox(
            height: compact ? 58 : 67,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 19),
              child: Row(
                children: [
                  if (!compact) ...[
                    for (final color in [
                      const Color(0xFFD88E87),
                      const Color(0xFFDEBD77),
                      const Color(0xFF88B2A1),
                    ])
                      Padding(
                        padding: const EdgeInsets.only(right: 7),
                        child: CircleAvatar(backgroundColor: color, radius: 5),
                      ),
                    const SizedBox(width: 22),
                  ],
                  const Icon(Icons.apps_rounded, color: Brand.ink),
                  const SizedBox(width: 12),
                  const Expanded(
                    child: Text(
                      'Aplicativos',
                      style: TextStyle(
                        color: Brand.ink,
                        fontSize: 21,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  IconButton(
                    onPressed: _nativeAvailable
                        ? _refreshNativeApps
                        : () => notify('Catálogo ilustrativo do protótipo'),
                    icon: const Icon(Icons.refresh, color: Brand.ink),
                  ),
                  IconButton(
                    key: const Key('close-browser'),
                    onPressed: () => setState(() => browserOpen = false),
                    icon: const Icon(Icons.close, color: Brand.ink),
                  ),
                ],
              ),
            ),
          ),
          const Divider(height: 1, color: Color(0xFFDCDAD6)),
          Expanded(
            child: LayoutBuilder(
              builder: (context, box) {
                final side = box.maxWidth >= 680;
                return Row(
                  children: [
                    if (side)
                      Container(
                        width: box.maxWidth >= 900 ? 210 : 185,
                        color: Brand.dark,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Padding(
                              padding: EdgeInsets.fromLTRB(20, 25, 0, 15),
                              child: Text(
                                'BIBLIOTECA',
                                style: TextStyle(
                                  color: Brand.muted,
                                  fontSize: 11,
                                  letterSpacing: 2,
                                ),
                              ),
                            ),
                            // The category list must scroll in short desktop
                            // windows (e.g. 1024x600 DeX-like viewport).
                            // A fixed Column overflows before it can shrink.
                            Expanded(
                              child: ListView(
                                key: const Key('category-list'),
                                padding: EdgeInsets.zero,
                                children: [
                                  for (final cat in _categories)
                                    _sideItem(cat),
                                ],
                              ),
                            ),
                            const Padding(
                              padding: EdgeInsets.all(18),
                              child: Text(
                                'DUMONT  /  01',
                                style: TextStyle(
                                  color: Brand.muted,
                                  fontSize: 10,
                                  letterSpacing: 2,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    Expanded(
                      child: Padding(
                        padding: EdgeInsets.fromLTRB(
                          compact ? 13 : 25,
                          compact ? 16 : 25,
                          compact ? 13 : 23,
                          10,
                        ),
                        child: LayoutBuilder(
                          builder: (context, available) {
                            final content = Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                if (!compact) ...[
                              const Text(
                                'Seu universo, organizado.',
                                style: TextStyle(
                                  color: Brand.ink,
                                  fontWeight: FontWeight.w600,
                                  fontSize: 23,
                                ),
                              ),
                              const SizedBox(height: 6),
                            ],
                            Text(
                              _nativeAvailable
                                  ? 'Aplicativos disponíveis neste dispositivo.'
                                  : 'Uma experiência feita para seu ritmo.',
                              style: const TextStyle(
                                color: Color(0xFF8E8C92),
                                fontSize: 12,
                              ),
                            ),
                            const SizedBox(height: 19),
                            TextField(
                              key: const Key('search-input'),
                              onChanged: (v) => setState(() => search = v),
                              decoration: InputDecoration(
                                isDense: true,
                                hintText: 'Buscar aplicativos',
                                prefixIcon: const Icon(Icons.search_rounded),
                                filled: true,
                                fillColor: Colors.white,
                                enabledBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: const BorderSide(
                                    color: Color(0xFFDCDAD6),
                                  ),
                                ),
                                focusedBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                  borderSide: const BorderSide(
                                    color: Brand.dark,
                                  ),
                                ),
                              ),
                            ),
                            if (!side)
                              SizedBox(
                                height: 53,
                                child: ListView(
                                  scrollDirection: Axis.horizontal,
                                  children: _categories
                                      .map(
                                        (cat) => Padding(
                                          padding: const EdgeInsets.only(
                                            right: 5,
                                          ),
                                          child: ChoiceChip(
                                            label: Text(cat),
                                            selected: category == cat,
                                            onSelected: (_) =>
                                                setState(() => category = cat),
                                            selectedColor: Brand.accent,
                                          ),
                                        ),
                                      )
                                      .toList(),
                                ),
                              ),
                            const SizedBox(height: 16),
                            Row(
                              children: [
                                const Expanded(
                                  child: Text(
                                    'Todos os aplicativos',
                                    style: TextStyle(
                                      color: Brand.ink,
                                      fontWeight: FontWeight.w600,
                                      fontSize: 14,
                                    ),
                                  ),
                                ),
                                Text(
                                  '${visible.length} itens',
                                  style: const TextStyle(
                                    color: Color(0xFF9998A0),
                                    fontSize: 12,
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 12),
                            Expanded(
                              child: LayoutBuilder(
                                builder: (context, dims) {
                                  final cols =
                                      (dims.maxWidth ~/ (compact ? 94 : 119))
                                          .clamp(2, 6);
                                  return GridView.builder(
                                    key: const Key('app-grid'),
                                    itemCount: visible.length,
                                    gridDelegate:
                                        SliverGridDelegateWithFixedCrossAxisCount(
                                          crossAxisCount: cols,
                                          childAspectRatio: .95,
                                          mainAxisSpacing: 12,
                                          crossAxisSpacing: 6,
                                        ),
                                    itemBuilder: (context, i) =>
                                        _appTile(visible[i]),
                                  );
                                },
                              ),
                            ),
                              ],
                            );
                            // Short multi-window surfaces cannot fit a full
                            // catalog header, filters and app grid at once.
                            // Scroll content only in this compact-height mode;
                            // normal tablet/desktop layouts remain unchanged.
                            if (available.maxHeight < (compact ? 245 : 260)) {
                              return SingleChildScrollView(
                                key: const Key('short-browser-scroll'),
                                child: SizedBox(
                                  height: compact ? 270 : 320,
                                  child: content,
                                ),
                              );
                            }
                            return content;
                          },
                        ),
                      ),
                    ),
                  ],
                );
              },
            ),
          ),
          if (!compact)
            SizedBox(
              height: 34,
              child: Padding(
                padding: const EdgeInsets.only(left: 23),
                child: Align(
                  alignment: Alignment.centerLeft,
                  child: Text(
                    _nativeAvailable
                        ? 'ANDROID  •  Catálogo via Kotlin'
                        : 'PRÉVIA FLUTTER  •  Integração Android via Kotlin',
                    style: const TextStyle(
                      color: Color(0xFF909099),
                      fontSize: 10,
                    ),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _sideItem(String cat) => Padding(
    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
    child: Material(
      color: cat == category
          ? Brand.cream.withValues(alpha: .16)
          : Colors.transparent,
      borderRadius: BorderRadius.circular(10),
      child: InkWell(
        key: Key('category-$cat'),
        borderRadius: BorderRadius.circular(10),
        onTap: () => setState(() => category = cat),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 11),
          child: Row(
            children: [
              Icon(
                switch (cat) {
                  'Todos' => Icons.grid_view_rounded,
                  'Recentes' => Icons.history_rounded,
                  'Samsung' => Icons.devices_rounded,
                  'Google' => Icons.public_rounded,
                  _ => Icons.settings_outlined,
                },
                color: Brand.cream,
                size: 19,
              ),
              const SizedBox(width: 12),
              Flexible(
                child: Text(
                  cat,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(color: Brand.cream, fontSize: 13),
                ),
              ),
            ],
          ),
        ),
      ),
    ),
  );

  Widget _appTile(DemoApp app) => InkWell(
    onTap: () => _launchApp(app),
    borderRadius: BorderRadius.circular(13),
    child: Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Container(
          height: 60,
          width: 60,
          decoration: BoxDecoration(
            color: app.tint.withValues(alpha: .17),
            borderRadius: BorderRadius.circular(16),
          ),
          child: app.iconPng == null
              ? Icon(app.icon, size: 33, color: app.tint)
              : Image.memory(
                  app.iconPng!,
                  key: Key('native-icon-${app.component}'),
                  width: 56,
                  height: 56,
                  filterQuality: FilterQuality.medium,
                  errorBuilder: (_, _, _) =>
                      Icon(app.icon, size: 33, color: app.tint),
                ),
        ),
        const SizedBox(height: 10),
        Text(
          app.name,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: const TextStyle(color: Brand.ink, fontSize: 12),
        ),
      ],
    ),
  );

  Widget _dock(bool compact) {
    const icons = [
      Icons.grid_view_rounded,
      Icons.folder_rounded,
      Icons.public_rounded,
      Icons.music_note_rounded,
      Icons.chat_bubble_rounded,
      Icons.settings_rounded,
    ];
    const names = [
      'Aplicativos',
      'Arquivos',
      'Navegador',
      'Música',
      'Mensagens',
      'Ajustes',
    ];
    return Container(
      key: const Key('dumont-dock'),
      height: compact ? 72 : 79,
      width: compact ? double.infinity : 630,
      decoration: BoxDecoration(
        color: Brand.dark.withValues(alpha: .96),
        border: Border.all(color: Brand.cream.withValues(alpha: .16)),
        borderRadius: BorderRadius.circular(22),
        boxShadow: const [
          BoxShadow(
            blurRadius: 22,
            offset: Offset(0, 10),
            color: Color(0x50000000),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: List.generate(
          icons.length,
          (i) => IconButton(
            key: Key('dock-$i'),
            icon: Icon(icons[i], color: Brand.cream, size: compact ? 27 : 32),
            onPressed: () {
              if (i == 0) {
                setState(() => browserOpen = true);
              } else if (i == 5) {
                _openLinus();
              } else {
                notify('${names[i]}: integração Kotlin pendente');
              }
            },
          ),
        ),
      ),
    );
  }

  Widget _ghost() => Material(
    key: const Key('ghost-panel'),
    color: Brand.dark,
    borderRadius: BorderRadius.circular(20),
    elevation: 20,
    child: Padding(
      padding: const EdgeInsets.all(22),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(Icons.tune_rounded, color: Brand.cream),
              const SizedBox(width: 13),
              const Expanded(
                child: Text(
                  'Ghost',
                  style: TextStyle(
                    fontSize: 22,
                    color: Brand.cream,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              IconButton(
                key: const Key('ghost-close'),
                tooltip: 'Fechar Ghost',
                icon: const Icon(Icons.close, color: Brand.cream),
                onPressed: () => setState(() => ghostOpen = false),
              ),
            ],
          ),
          const SizedBox(height: 7),
          const Text(
            'PAINEL LATERAL',
            style: TextStyle(
              color: Brand.muted,
              fontSize: 11,
              letterSpacing: 1.5,
            ),
          ),
          const SizedBox(height: 15),
          Expanded(
            child: ListView(
              padding: EdgeInsets.zero,
              children: [
                _ghostRow(
                  Icons.notifications_none,
                  'Notificações',
                  _notificationSnapshot?.accessGranted == true
                      ? (_notificationSnapshot!.connected
                            ? '${_notificationSnapshot!.notifications.length} registros locais · toque para atualizar'
                            : 'Acesso concedido · aguardando serviço Android')
                      : 'Acesso não concedido · toque para abrir permissões',
                  onTap: _handleNotificationTap,
                ),
                if (_notificationSnapshot?.connected == true)
                  ..._notificationSnapshot!.notifications
                      .take(2)
                      .map(
                        (item) => Padding(
                          padding: const EdgeInsets.only(left: 36, bottom: 8),
                          child: Text(
                            item.appLabel,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(
                              color: Brand.cream,
                              fontSize: 11,
                            ),
                          ),
                        ),
                      ),
                _ghostRow(
                  Icons.wifi,
                  'Conexões',
                  _deviceInfo?.networkType == null
                      ? 'Estado desconhecido'
                      : 'Rede ativa: ${_deviceInfo!.networkType}',
                  onTap: () => _openPublicSettings('internet'),
                ),
                _ghostRow(
                  Icons.devices,
                  'Dispositivo',
                  _deviceInfo == null
                      ? 'Dispositivo não verificado'
                      : '${_deviceInfo!.model} · Android ${_deviceInfo!.androidVersion}',
                  onTap: () => _openPublicSettings('display'),
                ),
                _ghostRow(
                  Icons.battery_std_rounded,
                  'Bateria',
                  _deviceInfo?.batteryPercent == null
                      ? 'Estado desconhecido'
                      : '${_deviceInfo!.batteryPercent}% · ${_deviceInfo!.isCharging == true ? "Carregando" : "Status medido"}',
                  onTap: _refreshDeviceStatus,
                ),
                _ghostRow(
                  Icons.shield_outlined,
                  'Privacidade',
                  'Configurações públicas do Android',
                  onTap: () => _openPublicSettings('privacy'),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'PRÉVIA — SEM CONTEÚDO DE MENSAGENS',
            style: TextStyle(
              color: Brand.muted,
              fontSize: 10,
              letterSpacing: 1,
            ),
          ),
        ],
      ),
    ),
  );
  Widget _ghostRow(
    IconData icon,
    String title,
    String caption, {
    VoidCallback? onTap,
  }) => InkWell(
    onTap: onTap,
    borderRadius: BorderRadius.circular(8),
    child: Padding(
      padding: const EdgeInsets.only(bottom: 18),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, color: Brand.cream, size: 22),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: const TextStyle(
                    color: Brand.cream,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const SizedBox(height: 5),
                Text(
                  caption,
                  style: const TextStyle(color: Brand.muted, fontSize: 11),
                ),
              ],
            ),
          ),
        ],
      ),
    ),
  );
}

class WallpaperPainter extends CustomPainter {
  const WallpaperPainter();
  @override
  void paint(Canvas canvas, Size size) {
    canvas.drawColor(Brand.dark, BlendMode.src);
    final top = Path()
      ..moveTo(size.width * .5, 0)
      ..cubicTo(
        size.width * .38,
        size.height * .3,
        size.width * .93,
        size.height * .17,
        size.width,
        size.height * .4,
      )
      ..lineTo(size.width, 0)
      ..close();
    canvas.drawPath(top, Paint()..color = Brand.accent.withValues(alpha: .24));
    final main = Path()
      ..moveTo(0, size.height * .76)
      ..cubicTo(
        size.width * .27,
        size.height * .47,
        size.width * .55,
        size.height * .94,
        size.width,
        size.height * .58,
      )
      ..lineTo(size.width, size.height)
      ..lineTo(0, size.height)
      ..close();
    canvas.drawPath(main, Paint()..color = Brand.cream.withValues(alpha: .19));
    final lower = Path()
      ..moveTo(0, size.height * .94)
      ..cubicTo(
        size.width * .42,
        size.height * .65,
        size.width * .65,
        size.height,
        size.width,
        size.height * .76,
      )
      ..lineTo(size.width, size.height)
      ..lineTo(0, size.height)
      ..close();
    canvas.drawPath(lower, Paint()..color = Brand.ink.withValues(alpha: .79));
  }

  @override
  bool shouldRepaint(covariant WallpaperPainter oldDelegate) => false;
}
