# ADR-0008 — Correção de ergonomia v1.0-dev

Data: 16/10/2026. Estado: desenvolvimento; testes no aparelho pendentes.

## Correções desta implementação
- Trilho lateral sempre visível com fundo carvão e opacidade visual mínima de 88%, independentemente da transparência solicitada às barras, preservando contraste e área de toque.
- Notificações em mural de cartões com título, descrição, app de origem e rolagem; conteúdo obtido somente pelo listener autorizado do Android, sem guardar ou enviar mensagens.
- Catálogo ampliado até a área entre topbar e dock, com busca e rolagem LazyVerticalGrid; filtros Todos, Samsung, Google, Sistema. Nenhum aplicativo é apagado ou desativado; Todos mantém a lista integral das atividades launchable autorizadas.
- Barra superior, Dock e trilho lateral permanecem funcionais; a Samsung continua HOME padrão e app Pilot é o único alvo de atualização.

## Limites declarados
- Samsung DeX e seu título de janela pertencem à One UI, fora da Activity DUMONT. Não existe API pública para fundir launchers. Adaptar layouts redimensionáveis e permitir alternar à Samsung é o caminho suportado.
- Botões Voltar, Home e Recentes são SystemUI. Não remover por ADB nem pedir permissões privilegiadas. Navegação por gestos pode ser escolhida pelo proprietário nas Configurações oficiais Android/Samsung.
- O quadrado de aplicativos recentes e a faixa branca precisam ser identificados pela captura/foco de janela antes de qualquer correção; não supor que sejam criados pelo DUMONT.
- Termux continua no menu Terminal e nunca é forçado ao Dock.
- Testes de bateria, Android A55, DeX maximizado, mural com volume de notificações, acessibilidade e gestos de abrir/recolher estão pendentes.

## Gates
| ID | Aceite | Estado |
|---|---|---|
| UI-01 | Trilho sempre visível, mesmo quando recolhido | Pendente aparelho |
| UI-02 | Mural com notificações reais autorizado, sem conteúdo em log | Pendente aparelho |
| UI-03 | Todos mostra todo catálogo inicializável, filtros funcionam | Pendente aparelho |
| UI-04 | Painel usa espaço vertical entre barras, rolagem acessível | Pendente aparelho |
| SAFE-01 | HOME Samsung preservada, sem apagar dados | Pendente aparelho |
| BUILD | CI, testes e assinatura estável | Pendente |
