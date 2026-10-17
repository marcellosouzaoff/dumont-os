# ADR-0013 — Fase 3: janelas internas Flutter (experimental)

**Estado:** código proposto na branch de laboratório; não considerar aprovado
antes de CI Flutter e teste físico no Tab S9 FE.
**Data:** 17/10/2026.

## Decisão

- Paleta 01 lida de design/tokens.json: Baritone Blue #272836,
  Cloud Dancer #F0EEE9, Curds & Whey #BCA483.
- DumontTheme centraliza tokens e cores de status compatíveis com governança.
- DumontStateBadge explicita success, warning, error e unknown;
  unknown é a opção inicial segura.
- DumontWindowManager cria, seleciona, move, redimensiona e fecha
  painéis **dentro da área Flutter**, respeitando o viewport.
- Primeira janela: Linus Security demonstra status Knox UNKNOWN sem
  reivindicar integração nativa, permissões ou proteção adicional.

## Escopo e restrições

Janelas DeX/Android de outros aplicativos continuam geridas pelo sistema.
Não há APIs privadas, substituição de HOME, SystemUI, Knox ou firmware.
Catálogo de aplicativos reais e Flutter-Kotlin MethodChannel ainda PENDENTES.
APK existente não é sobrescrito antes de prova de CI e consentimento explícito.

## Evidências e próximos gates

- Testes adicionados em test/window_manager_test.dart e
  test/security_window_test.dart; resultados devem vir do workflow
  Flutter visual validation.
- Correção local visual pré-existente: texto editorial aparece somente
  quando o painel de aplicativos está recolhido.
- Revisão física, golden tests, screenshot atual e build ARM64:
  UNKNOWN até o pipeline retornar resultado verificável.
