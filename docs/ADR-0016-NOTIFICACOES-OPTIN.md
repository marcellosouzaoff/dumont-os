# ADR-0016 — Notificações Android com consentimento no DUMONT

**Data:** 17/10/2026
**Autorização:** usuário confirmou continuação e integração em conversa.
**Limite fundamental:** autorização em conversa permite desenvolver a
funcionalidade, mas **não concede** a permissão especial Android.

## Implementação

- Um `NotificationListenerService` é declarado no manifesto com
  `android.permission.BIND_NOTIFICATION_LISTENER_SERVICE` (permissão
  de *binding do sistema*, não uma permissão comum solicitada por APK).
- O DUMONT exibe o estado de permissão antes de qualquer listagem.
  Sem permissão: lista **vazia**, sem acesso presumido.
- Somente após toque explícito, a interface abre a tela oficial de
  acesso às notificações. O Android exige decisão do usuário.
  **Jamais autorizar por ADB, app ops ou edição de configurações.**
- Depois do consentimento do Android: estado volátil em memória,
  máximo de 12 itens, apenas nome/identificador do aplicativo e
  timestamp. **Sem título, corpo, OTP, ações, extras ou anexos**;
  nenhum upload, telemetria ou persistência.
- Ao retirar acesso, desconectar o listener ou terminar o serviço,
  os itens em memória deixam de ser disponibilizados. O acesso é
  verificado em cada consulta.
- Manifesto limita tipos padrão a conversas/alertas, desativa
  ongoing/silent. A API Android pode impor outras restrições por versão.
- O Android adverte que um listener pode acessar dados sensíveis.
  O escopo de código minimiza tratamento, mas **não reduz o alcance
  técnico do privilégio concedido pelo sistema**. Explicar isso ao usuário.
- O painel Ghost exibe apenas nomes de até dois aplicativos e um
  indicador de quantidade; não mostra mensagens.
- Sem root, Knox, firmware, SystemUI ou papel HOME.

## Gates

1. TDD Flutter: testar negação, filtragem, seleção segura e abertura
   de configurações somente por ação explícita.
2. Flutter analyze e testes/goldens.
3. Build Android ARM64 debug e conferência de manifesto.
4. No último passo, comparar assinatura do APK atual com a nova;
   se incompatível, não sobrescrever a instalação antiga.
5. Instalar e testar no tablet sem conceder automaticamente o privilégio
   de listener. Conteúdo real só após decisão manual nas configurações.

**Estado de prova:** implementação aguardando validação técnica e
instalação separada. Não inferir que acesso especial já foi concedido.
