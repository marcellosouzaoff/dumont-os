# Changelog — DUMONT

Eventos técnicos documentados, não promessa de publicação.

## 2026-10-17 — Flutter Experience (v0.1, protótipo local)

- Criação de `experimental/flutter_dumont` com paleta Baritone Blue
  `#272836`, Cloud Dancer `#F0EEE9` e acento `#BCA483`.
- Janela de apps demonstrativos, busca, filtros, Dock, TopBar,
  wallpaper vetorial e Ghost invisível quando fechado.
- Tipografia DM Sans / Cormorant Garamond; fontes oficiais SIL OFL
  obtidas por script (binários não versionados).
- Flutter 3.47.7: analisador PASS, 5 widgets PASS, 4 goldens,
  build Web release PASS, build APK ARM64 debug PASS.
- Pacote independente `br.com.dumont.experience.preview`.
- APK preparado, **não instalado**; integrações Kotlin e DeX pendentes.
- Commits Flutter não implicam publicação no Google Play nem distribuição.

## 2026-10-17 — Kotlin Pilot v1.2 (preparado)

- Aplicação da Paleta 01 em Jetpack Compose, Dock centralizado,
  TopBar, catálogo adaptativo e gesto Ghost.
- Build Android PASS e 9 testes unitários PASS.
- APK não instalado por assinatura estável ausente neste Termux
  e dispositivo ADB não pareado.

## 2026-10-16 — Kotlin Pilot v1.1 (histórico)

- Pilot isolado `br.com.dumont.launcher.pilot` instalado no
  Galaxy Tab S9 FE, com integração por APIs públicas Android.
- HOME Samsung e Knox preservados.
