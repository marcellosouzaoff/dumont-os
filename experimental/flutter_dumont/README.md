# DUMONT Flutter Experience — laboratório visual

**Versão experimental:** v0.1 (APK de build inicial com versionName 1.0.0).
**Estado:** preview v1.0.0 instalada no Tab S9 FE em 17/10; código da Fase 3 testado no Codespace, ainda NÃO instalado no tablet.
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
- 11 testes Flutter passaram em 17/10 (incluindo 6 novos); golden desktop e Ghost atualizados.
- Builds Web release e Android ARM64 debug passaram.

## O que NÃO está implementado

- Apps reais do tablet, ícones reais e abertura de apps externos.
- Serviço de notificações Android, permissões, uso ou wallpapers reais.
- Bridge `MethodChannel`/`EventChannel` para Kotlin e testes DeX.
- Instalação física do novo build Fase 3 no Tab S9 FE e qualquer preview no Galaxy A55.

O catálogo atual é demonstrativo. Nunca apresentar suas informações
como dados observados no aparelho.

## Segurança e pacotes

Pacote Android de teste: `br.com.dumont.experience.preview`.
O Kotlin Pilot segue isolado em `br.com.dumont.launcher.pilot`;
o original tem `br.com.dumont.launcher`. Não substituir nenhum deles
nem a HOME Samsung. Knox e firmware intactos.

Mais detalhes no [status técnico](../../docs/STATUS_2026-10-17.md)
e [ADR-0012](../../docs/ADR-0012-FLUTTER-VISUAL-PROTOTYPE.md).

## Fase 3 — em implementação

A Paleta 01 passa a ser centralizada em `lib/theme/dumont_theme.dart`,
componente de status em `lib/widgets/dumont_state_badge.dart` e gerenciamento
de **janelas internas** em `lib/windows/dumont_window_manager.dart`.
O botão **Ajustes** abre a janela de demonstração Linus Security com Knox
marcado `UNKNOWN`. Essas janelas são internas ao Flutter e não operam janelas
DeX ou aplicativos externos. `flutter analyze` e `flutter test`
(11/11) PASS no Codespace. Novo APK ARM64 debug: PASS (SHA-256 `ce7a1835cf43c860b4a166df936fdbbc20e285d3c13822d3bd2b7ce3c878dac8`).
CI automático e instalação/teste físico Fase 3 permanecem pendentes.

## Ponte Android nativa — etapa 1

O pacote Flutter Preview agora possui um `MethodChannel` (Kotlin)
para listar atividades `MAIN/LAUNCHER` exportadas e abrir somente
componentes novamente verificados pelo Android. O catálogo mostra
apps reais quando o canal responde; no navegador mantém a demonstração.
A grade nativa ainda usa **ícones genéricos**. Não há integrações de
notificações, Knox, SystemUI, papel de parede ou controle de janelas DeX.
A integração nova ainda requer instalação e testes físicos no SM-X510.

Build ARM64 debug com AndroidBridge Kotlin: **PASS** no Codespace,
SHA-256 `fe3d629c3991563703fc1f839b256fad19df123b5e17d00cb78d4cee4eaac47c`. O pacote ainda não foi
instalado nem testado fisicamente.
