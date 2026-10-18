"""Verify canonical DUMONT color tokens across JSON, CSS, and Flutter."""
import json
import re
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
tokens = json.loads((root / "design/tokens.json").read_text())
css = (root / "design/tokens.css").read_text()
flutter = Path(sys.argv[1]) if len(sys.argv) > 1 else root / "experimental/flutter_dumont/lib/theme/dumont_theme.dart"
if not flutter.is_file():
    raise SystemExit(f"FAIL: Flutter theme not found: {flutter}")

checks = [
    ("baritone", "--dumont-baritone", "baritoneBlue"),
    ("cloud", "--dumont-cloud", "cloudDancer"),
    ("accent", "--dumont-accent", "curdsAndWhey"),
    ("red", "--dumont-red", "stateError"),
    ("greenStatus", "--dumont-status-ok", "stateSuccess"),
    ("yellowStatus", "--dumont-status-warning", "stateWarning"),
    ("unknownStatus", "--dumont-status-unknown", "stateUnknown"),
]
dart = flutter.read_text()
failures = []
for token, css_var, dart_var in checks:
    expected = tokens["palette"][token]["value"].upper()
    cm = re.search(r"(?m)^\s*" + re.escape(css_var) + r":\s*(#[0-9a-fA-F]{6})\s*;", css)
    dm = re.search(r"\b" + re.escape(dart_var) + r"\s*=\s*Color\(0xFF([0-9a-fA-F]{6})\)", dart)
    observed_css = cm.group(1).upper() if cm else "MISSING"
    observed_dart = "#" + dm.group(1).upper() if dm else "MISSING"
    if expected != observed_css or expected != observed_dart:
        failures.append(f"{token}: JSON={expected}, CSS={observed_css}, Dart={observed_dart}")
if failures:
    raise SystemExit("FAIL\n" + "\n".join(failures))
print(f"PASS: {len(checks)} JSON/CSS/Dart color mappings")
