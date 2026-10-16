#!/usr/bin/env python3
"""DUMONT Android M2: static gate, not a substitute for a Gradle build."""
from __future__ import annotations
from pathlib import Path
import json
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent
failure = []
def check(condition, message):
    print(("PASS " if condition else "FAIL ") + message)
    if not condition:
        failure.append(message)

app = (ROOT / "app/build.gradle.kts").read_text()
plugins = (ROOT / "build.gradle.kts").read_text()
manifest_file = ROOT / "app/src/main/AndroidManifest.xml"
manifest = manifest_file.read_text()
tokens = json.loads((ROOT / "design/tokens.json").read_text())
ui = (ROOT / "app/src/main/java/br/com/dumont/launcher/ui/DumontHome.kt").read_text()
theme = (ROOT / "app/src/main/java/br/com/dumont/launcher/ui/DumontTheme.kt").read_text()
readme = (ROOT / "README.md").read_text()

for file in (manifest_file, *ROOT.glob("app/src/main/res/values/*.xml")):
    try:
        ET.parse(file)
        check(True, "well-formed XML " + file.name)
    except ET.ParseError:
        check(False, "well-formed XML " + file.name)

check('compileSdk = 36' in app and 'targetSdk = 36' in app, "API 36 compile/target")
check('applicationIdSuffix = ".pilot"' in app, "Stable local-signing pilot uses isolated package")
check('compose-bom:2026.06.00' in app and 'compose-bom:2026.09.00' not in app, "Compose 1.11-era BOM avoids API 37 mismatch")
check('com.android.application") version "8.13.2"' in plugins, "AGP 8.13.2")
check('org.jetbrains.kotlin.android") version "2.3.20"' in plugins, "Kotlin 2.3.20")
check('android.intent.category.HOME' in manifest and 'android.intent.category.DEFAULT' in manifest, "Android HOME role declared")
check('android.intent.category.LAUNCHER' in manifest, "App can be opened without selecting default HOME")
allowed_permissions = {"android.permission.PACKAGE_USAGE_STATS"}
declared_permissions = {
    e.attrib["{http://schemas.android.com/apk/res/android}name"]
    for e in ET.fromstring(manifest).findall("uses-permission")
}
check(declared_permissions <= allowed_permissions,
      "Only optional, user-granted Usage Access can be requested")
check(all(item not in manifest for item in ("QUERY_ALL_PACKAGES", "BIND_ACCESSIBILITY_SERVICE", "MANAGE_EXTERNAL_STORAGE", "DEVICE_ADMIN")), "No dangerous privileged app capabilities")
check(all(k in theme for k in ("F1EEE7", "4E5353", "252525", "A47864", "CD212A")), "Canonical palette present")
check(all(k in tokens["palette"] for k in ("cloud","charcoal","black","mocha","red")), "Design tokens present")
check(all(k in ui for k in ("DUMONT", "ApplicationWindow", "BottomDock", "LazyVerticalGrid", "NativeAppIcon")), "Main responsive UI modules present")
check('Finder' not in ui and 'com.apple.' not in ui, "No Apple app icon dependency")
check('Text("Início"' not in ui, "Top bar only shows DUMONT branding")
for item in ("app/src/main/java/br/com/dumont/launcher/MainActivity.kt",
             "app/src/test/java/br/com/dumont/launcher/data/AppRulesTest.kt",
             ".github/workflows/android.yml"):
    check((ROOT/item).is_file(), "Required source: "+item)
relative = re.findall(r"!?(?:\[[^\]]+\])\(([^)]+)\)", readme)
for link in relative:
    link = link.split("#",1)[0]
    if link and not link.startswith(("http:","https:","mailto:")):
        check((ROOT/link).exists(), "README link " + link)
print(f"\nStatic gate: {len(failure)} failures")
if failure:
    sys.exit(1)
print("NOTE: static PASS is not APK build PASS or physical-device PASS.")
