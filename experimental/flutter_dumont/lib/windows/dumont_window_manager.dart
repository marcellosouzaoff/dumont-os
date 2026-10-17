import 'dart:math' as math;
import 'package:flutter/material.dart';
import '../theme/dumont_theme.dart';

/// Janelas apenas dentro da superfície Flutter. Não controla tarefas DeX.
@immutable
class DumontWindow {
  const DumontWindow({
    required this.id,
    required this.title,
    required this.rect,
  });

  final String id;
  final String title;
  final Rect rect;

  DumontWindow withRect(Rect value) =>
      DumontWindow(id: id, title: title, rect: value);
}

/// Estado de janelas: abrir, focar, mover, redimensionar e fechar.
class DumontWindowManager extends ChangeNotifier {
  final List<DumontWindow> _windows = [];

  List<DumontWindow> get windows => List.unmodifiable(_windows);

  void open({
    required String id,
    required String title,
    Size initialSize = const Size(420, 300),
  }) {
    if (_windows.any((w) => w.id == id)) {
      bringToFront(id);
      return;
    }
    _windows.add(DumontWindow(
      id: id,
      title: title,
      rect: Rect.fromLTWH(52 + _windows.length * 24.0,
          32 + _windows.length * 20.0, initialSize.width, initialSize.height),
    ));
    notifyListeners();
  }

  void close(String id) {
    if (_windows.removeWhereAndCount(id) > 0) notifyListeners();
  }

  void bringToFront(String id) {
    final idx = _windows.indexWhere((w) => w.id == id);
    if (idx == -1 || idx == _windows.length - 1) return;
    final w = _windows.removeAt(idx);
    _windows.add(w);
    notifyListeners();
  }

  Rect geometryFor(String id, Size viewport) {
    final w = _windows.firstWhere((w) => w.id == id);
    return _clamp(w.rect, viewport);
  }

  void moveBy(String id, Offset delta, Size viewport) {
    _update(id, (rect) => _clamp(rect.shift(delta), viewport), viewport);
  }

  void resizeBy(String id, Offset delta, Size viewport) {
    _update(id, (rect) {
      final minW = math.min(280.0, math.max(1.0, viewport.width - 16));
      final minH = math.min(180.0, math.max(1.0, viewport.height - 16));
      final maxW = math.max(minW, viewport.width - 8 - rect.left);
      final maxH = math.max(minH, viewport.height - 8 - rect.top);
      final width = (rect.width + delta.dx).clamp(minW, maxW).toDouble();
      final height = (rect.height + delta.dy).clamp(minH, maxH).toDouble();
      return _clamp(Rect.fromLTWH(rect.left, rect.top, width, height), viewport);
    }, viewport);
  }

  void _update(String id, Rect Function(Rect) transform, Size viewport) {
    final idx = _windows.indexWhere((w) => w.id == id);
    if (idx < 0) return;
    final current = _windows[idx];
    final next = transform(_clamp(current.rect, viewport));
    if (next == current.rect) return;
    _windows[idx] = current.withRect(next);
    notifyListeners();
  }

  Rect _clamp(Rect rect, Size viewport) {
    const padding = 8.0;
    final availableWidth = math.max(1.0, viewport.width - 2 * padding);
    final availableHeight = math.max(1.0, viewport.height - 2 * padding);
    final width = rect.width.clamp(
      math.min(280.0, availableWidth), availableWidth,
    ).toDouble();
    final height = rect.height.clamp(
      math.min(180.0, availableHeight), availableHeight,
    ).toDouble();
    final left = rect.left.clamp(
      0.0, math.max(0.0, viewport.width - width),
    ).toDouble();
    final top = rect.top.clamp(
      0.0, math.max(0.0, viewport.height - height),
    ).toDouble();
    return Rect.fromLTWH(left, top, width, height);
  }
}

extension on List<DumontWindow> {
  int removeWhereAndCount(String id) {
    final before = length;
    removeWhere((window) => window.id == id);
    return before - length;
  }
}

/// Camada posicionada em área central; sem janelas não intercepta toques.
class DumontWindowLayer extends StatelessWidget {
  const DumontWindowLayer({
    super.key,
    required this.manager,
    required this.contentBuilder,
  });

  final DumontWindowManager manager;
  final Widget Function(DumontWindow window) contentBuilder;

  @override
  Widget build(BuildContext context) => LayoutBuilder(
        builder: (context, constraints) => AnimatedBuilder(
          animation: manager,
          builder: (context, _) {
            final viewport = constraints.biggest;
            return Stack(
              clipBehavior: Clip.hardEdge,
              children: [
                for (final window in manager.windows)
                  _buildWindow(context, window, viewport),
              ],
            );
          },
        ),
      );

  Widget _buildWindow(
    BuildContext context,
    DumontWindow window,
    Size viewport,
  ) {
    final rect = manager.geometryFor(window.id, viewport);
    return Positioned(
      key: Key('window-${window.id}'),
      left: rect.left,
      top: rect.top,
      width: rect.width,
      height: rect.height,
      child: Listener(
        onPointerDown: (_) => manager.bringToFront(window.id),
        child: Material(
          elevation: 14,
          clipBehavior: Clip.antiAlias,
          color: DumontTheme.cloudDancer,
          borderRadius: BorderRadius.circular(15),
          child: Column(
            children: [
              GestureDetector(
                key: Key('window-drag-${window.id}'),
                behavior: HitTestBehavior.opaque,
                onPanUpdate: (d) =>
                    manager.moveBy(window.id, d.delta, viewport),
                child: Container(
                  color: DumontTheme.baritoneBlue,
                  height: 45,
                  padding: const EdgeInsets.only(left: 13, right: 4),
                  child: Row(
                    children: [
                      const Icon(Icons.drag_indicator,
                          color: DumontTheme.curdsAndWhey, size: 18),
                      const SizedBox(width: 7),
                      Expanded(
                        child: Text(window.title,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: const TextStyle(
                                color: DumontTheme.cloudDancer,
                                fontWeight: FontWeight.w600)),
                      ),
                      IconButton(
                        key: Key('window-close-${window.id}'),
                        tooltip: 'Fechar ${window.title}',
                        icon: const Icon(Icons.close,
                            color: DumontTheme.cloudDancer, size: 20),
                        onPressed: () => manager.close(window.id),
                      ),
                    ],
                  ),
                ),
              ),
              Expanded(child: contentBuilder(window)),
              Align(
                alignment: Alignment.bottomRight,
                child: GestureDetector(
                  key: Key('window-resize-${window.id}'),
                  behavior: HitTestBehavior.opaque,
                  onPanUpdate: (d) =>
                      manager.resizeBy(window.id, d.delta, viewport),
                  child: const Padding(
                    padding: EdgeInsets.all(7),
                    child: Icon(Icons.open_in_full,
                        size: 15, color: DumontTheme.baritoneBlue),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
