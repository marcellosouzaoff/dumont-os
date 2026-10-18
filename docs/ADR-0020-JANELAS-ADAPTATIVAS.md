# ADR-0020 — Janelas Flutter adaptativas, maximizar e restaurar

**Data:** 17/10/2026
**Branch:** `feature/flutter-visual-prototype-20261017`
**Governança:** protótipo Flutter; sem permissões Android especiais, sem ROM/HOME/Knox/DeX.

## Problema e objetivo

O gerente interno de janelas (`DumontWindowManager`) permitia abrir,
focar, mover, redimensionar e fechar, mas não maximizar/restaurar.
Em formatos de tablet retrato/paisagem e telas de desktop, era difícil
aproveitar a área de trabalho sem perder o tamanho escolhido.

## Comportamento implantado

- `toggleMaximize(id)` maximiza/restaura a janela Flutter com margem
  interna de 8 pixels lógicos. `isMaximized(id)` informa estado real.
- Ao maximizar, preserva `rect` original sem modificá-lo e traz
  janela ao primeiro plano.
- Em troca de dimensões `viewport`, geometria maximizada é recalculada
  em `geometryFor`, sem depender de orientação física ou API DeX.
- Arraste e redimensionamento não modificam geometria de janela maximizada.
- Ao restaurar, aplica o `rect` anterior limitado ao viewport atual.
- Ao fechar, limpa estado maximizado; ao reabrir, janela nasce normal.
- Botão acessível `window-maximize-ID` com tooltip contextual
  «Maximizar» ou «Restaurar»; fechamento continua independente.
- A implementação inicial de duplo toque concorreu com o botão em
  teste Flutter. Removido o gesto concorrente, mantendo somente botão
  explícito, sem sacrificar arraste da barra de título.

## TDD e evidências

1. **RED:** testes novos não compilavam: `toggleMaximize` e
   `isMaximized` inexistentes.
2. Primeira implementação: teste de geometria passou, teste de
   acionamento do botão falhou. Removido duplo toque concorrente.
3. **GREEN:** `test/window_adaptive_test.dart`: **2/2 PASS**.
4. Flutter 3.47.7: `flutter analyze` **PASS, 0 issues**;
   `flutter test` **PASS, 31/31**.
5. Build Android ARM64 `flutter build apk --debug
   --target-platform android-arm64`: **PASS**; assinatura debug
   SHA-256 `c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46`, compatível com a trial;
   APK SHA-256 `1df7174259aa335f6d1f2084d916910b7b1b5d7a8729ac110e812aa00092e02c`.
6. ADB `install -r` para `br.com.dumont.experience.preview.trial`:
   **PASS**, atualização do pacote e MainActivity `am start`: PASS.
7. UIAutomator no Galaxy Tab S9 FE: botão «Maximizar Linus
   Security» com limites [690,225][774,303], após toque passou a
   «Restaurar Linus Security» [2077,183][2161,261].
   Toque de restaurar reexibiu «Maximizar Linus Security» na
   posição original: **PASS**.
8. Selo `Knox · UNKNOWN` permanece intacto. Notificações especiais
   continuam **NOT_GRANTED**.
9. Nenhuma mudança manual de rotação, rede, tela externa ou modo DeX
   foi executada. O comportamento em sessão real DeX é **UNKNOWN**.
10. Testes Flutter simulam mudanças de dimensões; isso não equivale
    a sessão real no Samsung DeX (**UNKNOWN**).

## Investigação somente leitura — prévia original

- `adb shell pm list packages -u` lista somente
  `br.com.dumont.experience.preview.trial`.
- `pm path br.com.dumont.experience.preview` não retorna APK.
- Tablet acessível: SM-X510, orientação atual de tela em paisagem;
  densidade física declarada de 280 dpi.
- A origem do desaparecimento do pacote original é **UNKNOWN**.
  Não foi feita desinstalação, reinstalação, root, reset, grant ou
  mudança de HOME/DeX.
