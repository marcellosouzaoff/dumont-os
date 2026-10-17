# ADR-0012 — DUMONT Flutter Experience visual laboratory

State: local prototype PASSED, not installed. Date: 2026-10-17.

## Approved visual foundations

- Baritone Blue #272836 (desktop, top bar, dock, Ghost).
- Cloud Dancer #F0EEE9 (content surfaces, contrasting text).
- Curds & Whey #BCA483 (subtle accent).
- DM Sans UI and Cormorant Garamond editorial (Google Fonts OFL, binary assets not committed).
- DUMONT brand in top left, floating Dock, responsive application window, local search/category selection, invisible collapsed Ghost.

## Architecture

A separate Flutter app under experimental/flutter_dumont, with its own Android
package ID br.com.dumont.experience.preview. No SystemUI service, no HOME role,
no root, no Samsung Knox hooks or special privileges. Existing Kotlin+Compose
Pilot remains unchanged. Future bridge through Android MethodChannels is
PENDING: no real app launch, notifications, usage, wallpaper or multitasking
integration in this prototype. All catalog data is synthetic.

## Evidence from Codespace on 2026-10-17

- Flutter 3.47.7 stable, Linux x64 Codespace and Dart analyzer: PASS (zero issues).
- flutter test: PASS 5/5 including desktop, 1000x700 tablet, 390x844
  mobile, Ghost hidden/gesture/open/close, filtering/search.
- Four golden renderings captured using Flutter renderer (not generated image).
- flutter build web --release: PASS.
- flutter build apk --debug --target-platform android-arm64: PASS.
- Device installation: NOT PERFORMED; physical Samsung DeX compatibility UNKNOWN.
- GitHub remote push: NOT PERFORMED; logged-in account has READ permission.

## Required gate before deployment

1. Native Kotlin/Android bridge, deny actions without declared permissions.
2. Check APK manifest, ensure the new preview application ID is isolated.
3. Verify tablet ADB authorization and device identity SM-X510.
4. Request specific authorization before installing separate Flutter Preview APK.
5. Validate landscape/portrait, window resizing, DeX, memory, opening apps.
6. Never replace DUMONT Pilot or Android HOME automatically.
