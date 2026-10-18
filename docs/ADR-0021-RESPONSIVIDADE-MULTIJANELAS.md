# ADR-0021 — Responsividade celular, tablet e janelas curtas

**Data:** 17/10/2026
**Branch:** `feature/flutter-visual-prototype-20261017`
**Compromissos:** orçamento zero, Flutter + Kotlin, sem modificar HOME/Knox, sem concessões especiais Android.

## Problema reproduzido (TDD RED)

O catálogo Flutter ocupava alturas fixas:
- `1024 × 600` dp: `RenderFlex overflowed by 54 pixels on the bottom`.
  A barra lateral de categorias era uma `Column` não rolável.
- `600 × 360` dp: `RenderFlex overflowed by 83 pixels on the bottom`.
  Cabeçalho, filtros, busca e grade excediam a janela compacta.

## Correção (GREEN)

- Barra lateral: categorias em `Expanded(ListView)`, com cabeçalho e
  rodapé preservados, permitindo rolar filtros em janelas baixas.
- Conteúdo principal do navegador: `LayoutBuilder` seleciona
  `SingleChildScrollView` em alturas insuficientes, com área interna
  mínima para busca, categorias e grade.
- Layout habitual de tablet/desktop permanece como antes;
  o caminho de rolagem especial é exclusivo para altura curta.
- Sem chamadas privilegiadas Kotlin, sem polling, sem mudança do sistema,
  sem instalação fora do pacote experimental `.trial`.

## Matriz Flutter verificada

Larguras × alturas lógicas cobertas pelos novos testes:
`320×568`, `360×640`, `390×844`, `412×915` (classe A55 simulada),
`600×960`, `600×360`, `690×700`, `800×480`, `800×1280`,
`1024×600`, `1279×720`, `1280×800`, `1920×1080`.
- Mudança de viewport em execução com catálogo e dock presentes;
  Ghost abre/fecha em 320×568 sem overflow.
- `flutter analyze`: PASS, 0 problemas.
- `flutter test`: PASS, 33/33 (inclui goldens anteriores).
- Alteração de escala de fonte e teclado do Android não contemplados
  nestes testes; não presumir cobertura.

## Dispositivos reais

- Galaxy Tab S9 FE SM-X510: ADB ativo, prévia
  `br.com.dumont.experience.preview.trial` já instalada de fase anterior.
- Galaxy A55 SM-A556E: acessível por Remote Desktop Commander/Termux,
  porém `adb devices` sem destinos; instalação DUMONT não confirmada.
  Teste físico no A55: **UNKNOWN**. Não instalar sem caminho de acesso
  e verificação de certificado/pacote.
- Sessão Samsung DeX: **UNKNOWN**, não ativada, não reivindicar PASS com
  base em testes de viewport Flutter.
- Pacote original `br.com.dumont.experience.preview` no tablet:
  ausente em auditorias anteriores; causa **UNKNOWN**,
  sem reinstalação/desinstalação nesta rodada.

## Build Android e backup

- Build `flutter build apk --debug --target-platform android-arm64`:
  **PASS**. Identificador `br.com.dumont.experience.preview.trial`;
  SHA-256 do APK `869386ef846ddb991b789cea47faabca7b58b7e9174e87c83e688b02338a0ead`.
- Certificado SHA-256 `c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46`,
  igual à prévia trial anterior.
- Transferência ao Termux com SHA verificado; `adb install -r`:
  **Success**, Activity iniciada por `am start -W` (PASS).
- Galaxy Tab S9 FE UIAutomator: DUMONT presente, catálogo visível,
  bateria 80%; Ghost abriu, mostrou rede e modelo SM-X510;
  notificações continuam sem acesso especial (PASS).
- **Limites:** não foi alterado tamanho físico, orientação nem ativado
  DeX. Os casos de overflow em 1024×600 e 600×360 são comprovados
  por teste Flutter, não por sessão DeX física.
- A55 permanece sem ADB ativo, portanto físico **UNKNOWN**.
- A prévia original não consta no Android, causa **UNKNOWN**;
  nenhum reinstalar/remover foi executado.
- Backup de código gerado por `git archive` após o commit final da
  rodada, verificar integridade e localizar no tablet.
- Envio de ZIP a Google Drive: somente declarar PASS após a
  confirmação do binário no Drive, distinta do upload de relatório.
