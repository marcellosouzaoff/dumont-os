# DUMONT Flutter Experience — laboratório visual

**Versão experimental:** v0.1 (APK de build inicial com versionName 1.0.0).
**Estado:** UI Flutter compilada e testada, ainda NÃO instalada.
**Não é ROM, SystemUI nem launcher HOME.**

## Design aprovado

| Token | Cor | HEX | Uso |
|---|---|---|---|
| Baritone Blue | escuro | `#272836` | Desktop, TopBar, Dock e Ghost |
| Cloud Dancer | creme | `#F0EEE9` | Conteúdo e texto claro |
| Curds & Whey | acento | `#BCA483` | Elementos discretos |

Tipografia DM Sans para a interface, Cormorant Garamond para editorial.
Binários não versionados; licenças SIL OFL incluídas.

## Executar no Codespace / Linux

Pré-requisitos: Flutter SDK (validado com 3.47.7). Android SDK API 36
e Java 21 para APK. Navegador compatível para `flutter run -d chrome`.

~~~bash
cd experimental/flutter_dumont
bash scripts/setup_fonts.sh
flutter pub get
flutter analyze
flutter test
flutter run -d chrome
flutter build web --release
flutter build apk --debug --target-platform android-arm64
~~~

`flutter test --update-goldens` só quando houver alteração
intencional da UI; em CI/regressão usar `flutter test`.
O script prepara os assets de fonte sem precisar armazená-los no Git.

## O que está implementado

- Barra DUMONT, Dock, papel de parede vetorial, painel Aplicativos,
  pesquisa, filtro por categoria, grid responsivo e Ghost.
- Gesto na borda para abrir o Ghost; recolhido fica invisível.
- Golden renderizados pelo motor Flutter: desktop, tablet, celular
  e Ghost aberto, em `test/goldens/`.
- Cinco testes Flutter passaram, incluindo interação de busca/filtros.
- Builds Web release e Android ARM64 debug passaram.

## O que NÃO está implementado

- Apps reais do tablet, ícones reais e abertura de apps externos.
- Serviço de notificações Android, permissões, uso ou wallpapers reais.
- Bridge `MethodChannel`/`EventChannel` para Kotlin e testes DeX.
- Instalação física no Tab S9 FE ou Galaxy A55.

O catálogo atual é demonstrativo. Nunca apresentar suas informações
como dados observados no aparelho.

## Segurança e pacotes

Pacote Android de teste: `br.com.dumont.experience.preview`.
O Kotlin Pilot segue isolado em `br.com.dumont.launcher.pilot`;
o original tem `br.com.dumont.launcher`. Não substituir nenhum deles
nem a HOME Samsung. Knox e firmware intactos.

Mais detalhes no [status técnico](../../docs/STATUS_2026-10-17.md)
e [ADR-0012](../../docs/ADR-0012-FLUTTER-VISUAL-PROTOTYPE.md).
