import 'package:flutter/material.dart';

import '../theme/dumont_theme.dart';

enum DumontState { success, warning, error, unknown }

/// Um estado UNKNOWN nunca se transforma em PASS por aparência.
class DumontStateBadge extends StatelessWidget {
  const DumontStateBadge({
    super.key,
    required this.label,
    this.state = DumontState.unknown,
  });

  final String label;
  final DumontState state;

  @override
  Widget build(BuildContext context) {
    final (color, icon) = switch (state) {
      DumontState.success => (
        DumontTheme.stateSuccess,
        Icons.check_circle_outline_rounded,
      ),
      DumontState.warning => (
        DumontTheme.stateWarning,
        Icons.warning_amber_rounded,
      ),
      DumontState.error => (
        DumontTheme.stateError,
        Icons.error_outline_rounded,
      ),
      DumontState.unknown => (
        DumontTheme.stateUnknown,
        Icons.help_outline_rounded,
      ),
    };

    return Semantics(
      label: '$label, status ${state.name.toUpperCase()}',
      child: DecoratedBox(
        decoration: BoxDecoration(
          color: DumontTheme.white,
          border: Border.all(color: color.withValues(alpha: .4)),
          borderRadius: BorderRadius.circular(9),
        ),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 7),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(icon, size: 18, color: color),
              const SizedBox(width: 7),
              Flexible(
                child: Text(
                  '$label · ${state.name.toUpperCase()}',
                  style: const TextStyle(
                    fontFamily: 'DM Sans',
                    color: DumontTheme.baritoneBlue,
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
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
