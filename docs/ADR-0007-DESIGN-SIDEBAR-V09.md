# ADR-0007 — DUMONT v0.9: harmonia cromática, status e sidebar lateral

Data: 16/10/2026. Estado: implementação inicial sujeita a CI e verificação física.
Referência visual: duas capturas de sidebar vertical de fórum Discourse fornecidas pelo proprietário em conversa.

## Geometria canônica
- Papel de parede cobre a área completa da Activity.
- Barra superior e Dock inferior mantêm exatamente a mesma largura horizontal, cor sólida e opacidade ajustável em conjunto. Não mover Android SystemUI/DeX.
- Barra superior: à esquerda Aplicativos, Configurações, Terminal; no centro bateria com porcentagem sobreposta no ícone, data/hora, Wi-Fi; à direita marca DUMONT.
- Remover botão Galaxy e sino da barra superior.
- Os apps Samsung continuam no catálogo de Aplicativos normal, sem acesso privilegiado nem duplicação; demais apps launchable continuam disponíveis.
- Sidebar vertical fixa à direita, **estritamente entre** a barra superior e o Dock inferior. Trilho estreito de 60dp, com cinco opções: Notificações, Conexões, Dispositivo, Segurança e Sistema.
- Ao tocar no trilho, painel expande para a esquerda. Ao tocar na mesma seção, recolhe. Arrastar à esquerda abre, à direita recolhe. A interface não sobrepõe a HOME Samsung.
- Notificações reais dependem da autorização Android; conexões, dispositivo, segurança e sistema abrem telas oficiais Android/Samsung usando GalaxyBridge.
- A seção ativa permanece destacada sem usar cores de status falsas. Não clonar a aparência do fórum; a referência fornece apenas ergonomia do trilho.

## Harmonia cromática
- Dominante: preto carvão #252525 (barra superior, inferior e trilho lateral).
- Superfícies secundárias: grafite #4E5353; texto claro #F1EEE7 (ou branco em ícones críticos para contraste).
- Seleção/ação: mocha #A47864 como único realce recorrente, aplicado com transparência para não criar bloco visual pesado.
- Vermelho #CD212A reservado a alertas reais e exceções, jamais destaque padrão da navegação.
- Opacidade inicial das barras 100% (0% transparência), ajustável pelo proprietário no painel Aparência.
- Para wallpapers de alto contraste, respeitar fundo escuro nas barras para legibilidade. Nenhuma cor do SystemUI/Samsung é alterada.

## Gates e limitações
- UI-01: status real, bateria à esquerda e Wi-Fi à direita da data/hora — UNKNOWN em dispositivo.
- UI-02: Galaxy e sino removidos do topo — UNKNOWN em dispositivo.
- UI-03: trilho visível à direita com cinco ações — UNKNOWN.
- UI-04: recolhimento/expansão por toque e gesto horizontal — UNKNOWN.
- UI-05: controles Samsung abrem via intents públicos existentes — v0.8 Wi-Fi PASS; v0.9 UNKNOWN.
- UI-06: catálogo de aplicativos mantém apps Samsung disponíveis — revisão de código, teste pendente.
- SAFE-01: One UI permanece HOME; nenhum novo serviço privilegiado — validação pendente.
- PERF-01: evitar interceptar gestos globais do Android, consumo de bateria ainda não medido.
- CI-01: Gradle, testes unitários e assinatura estável — PENDENTE.
