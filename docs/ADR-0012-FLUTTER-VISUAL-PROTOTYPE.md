# ADR-0012 — DUMONT Flutter Experience visual laboratory

Estado: protótipo Flutter com PASS em testes e builds; NÃO INSTALADO.
Data: 17/10/2026. Evidências antes da integração Kotlin e do DeX real.

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
- GitHub remote push: pendente no momento da escrita original.
  Verificação posterior do GitHub indicou permissão WRITE para a conta
  conectada; o envio precisa ser comprovado separadamente.

## Required gate before deployment

1. Native Kotlin/Android bridge, deny actions without declared permissions.
2. Check APK manifest, ensure the new preview application ID is isolated.
3. Verify tablet ADB authorization and device identity SM-X510.
4. Request specific authorization before installing separate Flutter Preview APK.
5. Validate landscape/portrait, window resizing, DeX, memory, opening apps.
6. Never replace DUMONT Pilot or Android HOME automatically.

## Linha divisória entre renderização e instalação

`flutter test` usa o próprio Flutter para produzir os golden PNG. Não
comprova a abertura de janelas nativas Samsung, acesso a apps instalados
nem estabilidade no dispositivo físico. Nenhuma permissão especial,
nem HOME, foi adicionada ao protótipo.

## Registro e versões

- GitHub: `marcellosouzaoff/dumont-os`.
- Branch de laboratório: `feature/flutter-visual-prototype-20261017`.
- Primeiro commit local: `96b5b0c`.
- Build APK de prévia: `br.com.dumont.experience.preview`, debug ARM64.
- Fontes: licenças SIL OFL no repositório, assets obtidos por script.
- O conteúdo de `docs/STATUS_2026-10-17.md` é a referência para
  PASS/FAIL/UNKNOWN e instalações; não pressupor deploy.
