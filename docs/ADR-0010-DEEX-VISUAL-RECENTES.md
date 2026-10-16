# ADR-0010 — DUMONT v1.1.1: fluxo visual de retomada no DeX

Data: 16/10/2026. Estado: implementação inicial, CI e dispositivo pendentes.

- Ao tocar em um app do catálogo, o catálogo do DUMONT é fechado antes de o app receber foco. Ao voltar para o DUMONT, não há diálogo do catálogo escondendo o workspace.
- A seção **Recentes** no catálogo apresenta atividades inicializáveis com pontuação de uso conhecida, em ordem decrescente. É uma lista de atalhos, NÃO um inventário de janelas ou tarefas do DeX. O acesso de uso continua opcional.
- O botão **Todos** continua mostrando todos os apps inicializáveis. Samsung, Google, Sistema e busca são preservados.
- A política pública do ADR-0009 continua usando NEW_TASK/RESET_TASK_IF_NEEDED e evita múltiplas tarefas forçadas.
- Não mover/redimensionar janelas Samsung via interfaces privadas. Não ocultar os controles nativos de navegação Android.
- Não modificar HOME, Knox, firmware, DeX ou aplicativos Samsung.
- Gates: CI e teste de dois ciclos com catálogo fechado; seção Recentes com e sem UsageStats; Dock; layout DeX janela versus maximizado.
