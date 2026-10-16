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
