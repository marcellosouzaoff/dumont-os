# ADR-0009 — DUMONT Orchestrator v1.1 (DeX)

Data: 16/10/2026. Estado: primeira etapa implementada; teste físico pendente.

## Problema
A HOME Samsung/DeX e a Activity DUMONT são superfícies distintas. Iniciar aplicativos pelo DUMONT pode tornar visualmente aparente essa separação; um aplicativo comum não possui autoridade pública para fundir ou substituir o gerenciador de janelas da Samsung.

## Implementação de primeira etapa
- Política única para iniciar apps a partir do Dock, catálogo e Terminal: ACTION_MAIN + CATEGORY_LAUNCHER + componente explícito; flags NEW_TASK e RESET_TASK_IF_NEEDED.
- Sem MULTIPLE_TASK, CLEAR_TASK, CLEAR_TOP ou NO_HISTORY; não criar deliberadamente múltiplas tarefas nem destruir a pilha existente.
- Usar ícones nativos e componente retornado pelo PackageManager; não fixar classes internas Samsung e não iniciar atividades privadas.
- Não usar ActivityOptions.setLaunchWindowingMode, serviços privados Samsung, AppOps privilegiado, AccessibilityService, overlays persistentes, Device Owner ou comandos de janela por ADB.
- Continuar a adaptar a Activity DUMONT ao redimensionamento DeX existente, mantendo barras, área de aplicativos e Ghost.
- Não alterar HOME padrão, DeX, navegação do sistema, dados de outros apps, firmware ou Knox.

## Requisitos para próxima etapa
Testes de aplicativo conhecido (um único Chrome/Files), retorno via Android e reabertura do mesmo app, sem alteração de suas configurações ou leitura de dados. Diferenciar duplicação verdadeira de tarefa de múltiplas janelas já existentes no DeX. Diagnóstico via dumpsys activity/window com logs sanitizados. Caso a política não elimine o problema, solicitar descrição/captura do comportamento antes de introduzir novas flags.

## Gates
- G1: teste de unidade da política de flags sem destruição: pendente CI.
- G2: compilação e assinatura estável Pilot v1.1: pendente.
- G3: instalação Pilot no SM-X510 sem alterar HOME: não executada.
- G4: ida e volta de aplicativo sem janelas extras: não executada.
- G5: DeX e gerenciamento de janelas profundamente unificados: não implementado (limite Samsung).

## Validação física — 16/10/2026

- Build CI `38938989177` PASS: compilação, testes unitários e artifact.
- APK Pilot v1.1 SHA-256: `63c5455a2a123216e6ee40a9d4ed235e7f518fa8190c82779f66c2fd5ea40cd2`, certificado SHA-256 `7898a5f8177537852038b78de14115a4a3f1ef3cfc9bf9dd7174c56b413beda7`; verificação v3 PASS.
- Galaxy Tab S9 FE `SM-X510`: `adb install --no-streaming -r -t` retornou Success; Pilot versionCode 12.
- Preferências `dumont_dock` e `dumont_appearance` preservadas byte a byte, com backup privado no Termux. HOME Samsung `com.sec.android.app.launcher` não alterada; aplicativo original preservado.
- Primeiro teste: DUMONT → Aplicativos → Chrome abriu janela `com.android.chrome/com.google.android.apps.chrome.Main`. Tarefa Chrome existente `#13470` foi reutilizada, modo `freeform`. Não surgiu outra tarefa Chrome no inventário de tarefas.
- Reabertura do DUMONT: Android informou `its current task has been brought to the front`, sem criar uma nova instância do launcher.
- Segundo teste: Chrome foi reaberto a partir do catálogo, continuou na mesma tarefa `#13470`; retorno trouxe DUMONT ao primeiro plano. PASS para dois ciclos controlados.
- Não foram alteradas configurações, conteúdo do Chrome, dados do usuário, aplicações Samsung, navegação ou parâmetros DeX. Arquivos transitórios de UI foram limpos.
- Ainda UNKNOWN: outros aplicativos, maximização/tamanho de janelas, gestos no DeX, múltiplos monitores, autonomia e comportamento longo. A fusão com o gerenciador de janelas privado Samsung não foi tentada.
