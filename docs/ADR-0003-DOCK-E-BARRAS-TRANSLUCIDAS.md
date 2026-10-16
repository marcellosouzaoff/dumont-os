# ADR-0003 — Layout flutuante e Dock por uso — DUMONT v0.6

**Data:** 16/10/2026. **Escopo:** Galaxy Tab S9 FE e Galaxy A55. **Estado:** desenvolvimento; validação CI e dispositivo pendentes.

## Decisões do proprietário

1. Plano de fundo contínuo, preenchendo toda a área do aplicativo, por trás das barras superior/inferior. Insets do Android e indicadores reais do sistema continuam respeitados.
2. Barra superior flutuante, translúcida, com **Aplicativos**, **Configurações** (abre as configurações reais Android), **Terminal** (abre o Termux nativo quando disponível), e marca **DUMONT alinhada à direita**.
3. Ao tocar DUMONT, painel próprio de aparência permite ajustar **transparência de ambas as barras**, com controle local persistente. Padrão: 30% transparente; intervalo: 5% a 80%. Isso não altera a interface dos demais apps ou o SystemUI.
4. Barra inferior contém **somente aplicativos**: mais utilizados e os que o usuário fixar voluntariamente. Não incluir botão Apps; não fixar Termux ou Google automaticamente.
5. Ao instalar a atualização, os antigos favoritos gerados automaticamente pelo aplicativo não são tratados como escolhas manuais. Favoritos personalizados são preservados; app fixado permanece acima dos usados com maior frequência.
6. Toque longo em aplicativo do catálogo ou do Dock fixa/desafixa. Toque curto abre o aplicativo real instalado.
7. **Aplicativos** no menu abre o catálogo centralizado e rolável, inicialmente mostrando **todos os apps iniciáveis visíveis ao PackageManager**, sem filtro prévio Google. Busca por nome ou pacote. Sistemas internos e componentes sem atividade inicializável não serão fingidos como apps clicáveis; restrições de perfis/work profile seguem o Android.
8. Ranking automático local dos apps iniciados pelo DUMONT. Mediante concessão opcional de **Acesso ao uso**, ranking por tempo em primeiro plano nos últimos sete dias, incluindo apps abertos fora do DUMONT. Nenhuma telemetria, rede ou autorização silenciosa. O usuário pode revogar essa concessão nas Configurações Android.
9. DUMONT opera como activity do tipo HOME e não como serviço de sobreposição. A Samsung permanece como HOME padrão até decisão específica e teste de ergonomia/recuperação.

## Testes necessários

| ID | Teste | Critério |
|---|---|---|
| APP-01 | Lista inicial sem filtro | Exibe todos os apps iniciáveis que o sistema disponibiliza ao launcher |
| APP-02 | Abertura dos aplicativos | Componentes instalados abrem via intent e retornam ao workspace |
| TOP-01 | Ordenação e menu | Aplicativos, Configurações, Terminal à esquerda; DUMONT à direita |
| TOP-02 | Configurações Android | Abre a atividade real de configuração do dispositivo |
| TOP-03 | Terminal | Abre Termux real; mostra erro honesto se ausente |
| DOCK-01 | Limpeza | Sem ícone Apps nem Termux/Gmail inseridos automaticamente |
| DOCK-02 | Fixação e uso | App fixado persiste; apps iniciados pelo DUMONT entram no ranking |
| DOCK-03 | Dados antigos | Favoritos manuais preservados; defaults legados não forçados |
| VIS-01 | Wallpaper e transparência | Imagem completa atrás das barras, controle persistente e legibilidade |
| PRIV-01 | Acesso ao uso | Sempre opt-in; rankings locais sem dados enviados |
| SAFE-01 | HOME e privacidade | Nenhuma mudança automática do launcher Samsung, sem root/overlay permanente |
| PERF-01 | Memória e bateria | Ainda devem ser medidos nos dois dispositivos |

## Riscos abertos

- Nem todo pacote instalado tem atividade lançável; apps administrados por outros perfis dependem de políticas do Android.
- Estatísticas de apps no aparelho inteiro dependem de uma permissão especial que precisa ser concedida pelo usuário; sem ela somente uso via DUMONT é contabilizado.
- O posicionamento da barra e sua opacidade devem ser conferidos em retrato/paisagem; possível conflito com teclado, janelas freeform e acessibilidade.
- Não foi feito teste do dispositivo A55 com esta versão, nem medição comparativa de bateria.

**Implementação:** componentes Android Compose, armazenamento local de pins/contagens, sem modificação de arquivos do Termux, Google Play, aplicativos instalados ou dados Samsung.
