# ADR-0002 — DUMONT como workspace Android, com ergonomia desktop

**Data:** 16/10/2026. **Estado:** candidato a aceite visual e a testes reais. Implementação preliminar na branch de desenvolvimento.

## Problemas observados

- Margens grandes e área útil subaproveitada.
- Barra superior com nome DUMONT, mas sem menu operacional.
- Dock sem acesso explícito ao catálogo ou ao Termux.
- Painel central sempre aberto, bloqueando o espaço de trabalho.
- Travamento observado pelo proprietário ao iniciar. No código, a consulta aos aplicativos e a carga de ícones ocorriam sincronamente na thread principal, dentro de onResume. Possível causa, ainda sem prova exclusiva.
- Dúvidas sobre bateria e uso como painel sobre a Samsung.

## Proposta visual e de navegação (v0.5)

1. Barra superior com DUMONT, menu Aplicativos, atalho Termux se instalado e Atualizar. Status Android permanece real e independente.
2. Desktop limpo ao iniciar; nenhum painel permanente de aplicativos.
3. Catálogo aberto somente sob demanda via menu ou Dock, com pesquisa, Google/Todos, Atualizar e Fechar.
4. Dock inferior compacto e sempre visível, com Apps, Termux nativo instalado, seguido dos favoritos; não inventar app ausente.
5. Margens de 8dp no tablet e 4dp no smartphone, contra os 20dp anteriores; janela de apps até 98% da largura útil, com scroll e insets.
6. Integração de Termux por abertura direta do aplicativo real, sem root, sem transplantar seu runtime e sem privilégios artificiais.
7. Busca de aplicativos e carregamento de ícones em thread de fundo, com indicador de progresso, falha e atualização sob demanda.
8. DUMONT permanece um launcher Android normal. Somente com aprovação separada do usuário poderá assumir a HOME padrão; a Samsung permanece instalada e seus serviços sistêmicos não são desativados.
9. Proibir overlay permanente e serviços de polling desnecessários. Bateria e memória precisam de medição comparativa; não declarar economia sem evidência.
10. Ergonomia inspirada no macOS sem copiar Finder, marcas ou elementos gráficos proprietários.

## Gates de aceitação

| Gate | Critério | Estado |
|---|---|---|
| UX-01 | Workspace inicia sem painel bloqueador | Não testado |
| UX-02 | Barra superior funcional e legível | Não testado |
| UX-03 | Dock mostra Apps + Termux quando instalado | Não testado |
| UX-04 | Catálogo abre/fecha sem travar e busca funciona | Não testado |
| UX-05 | Bordas reduzidas, sem recortes e áreas clicáveis ≥48dp | Não testado |
| PERF-01 | PackageManager fora da thread da interface | Auditoria de código/CI pendente |
| PERF-02 | Consumo de bateria comparado ao launcher Samsung | UNKNOWN |
| SAFE-01 | Nenhuma alteração automática do launcher padrão | Revisão de código/manifesto pendente |
| SAFE-02 | Atalho HOME e configurações Samsung recuperáveis | Teste em aparelho pendente |

## Rollback

Com ADB previamente autorizado, pressionar HOME pelo ADB e encerrar o processo Pilot, sem remover dados. O DUMONT original, Termux, apps Play Store e demais aplicativos devem permanecer intactos.

## Relação com v0.3

Esta ADR atualiza requisitos de ergonomia do PRD, TRD e Design System v0.3, sem alterar a paleta e as diretrizes de segurança. Mudanças de HOME e instalações não fazem parte desta revisão de código.
