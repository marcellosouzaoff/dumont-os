# ADR-0024 — Galaxy A55: catálogo, Ghost e gesto de borda

**Data:** 17/10/2026
**Estado:** AUDITORIA / investigação pendente

## Evidências verificáveis

- Pareamento ADB `192.168.1.7:41733`: **PASS**, modelo
  `SM-A556E`, pacote `br.com.dumont.experience.preview.trial`
  instalado no usuário 0 e Samsung HOME mantida.
- `am start -W` retornou **Status ok**.
- UIAutomator: «DUMONT», «Aplicativos», janela «Linus Security»,
  lista «Todos os aplicativos» com **69 itens**: **PASS**.
  A auditoria registra somente a contagem; não lista apps privados.
- No Linus Security, Knox aparece como **UNKNOWN**, sem nenhuma
  alteração de segurança: **PASS**.
- `settings get secure enabled_notification_listeners` contém
  `br.com.dumont.experience.preview.trial/...DumontNotificationListener`.
  Estado no Android: **ENABLED**. Origem da concessão: **UNKNOWN**.
  Nenhuma leitura de conteúdos de notificações nem concessão efetuada.
- O elemento «Abrir painel Ghost» foi localizado nos limites
  `[906,211][1080,2059]`. Um toque via `adb input tap`
  no trilho direito abriu a cortina de notificações Android,
  e não permitiu comprovar a abertura do Ghost.
- Tentativa de fechamento/navegação posterior retornou a outro app
  em primeiro plano. Os testes desse momento foram interrompidos;
  nenhuma informação exibida no outro app deve compor auditoria.
- Resultado **Ghost físico no A55: UNKNOWN/FAIL de interação**,
  requer revisão da área clicável e conflito com gestos de borda.
  Não declarar como PASS apenas porque o controle aparece.
- Nenhuma instalação adicional, root, mudança de HOME, alteração de
  Wi-Fi, rotações, permissões ou abertura de mensagens privadas.

## Critério para próximo ciclo

1. Teste widget específico reproduzindo toque no trilho Ghost em
   viewport de classe celular, com estados aberto/fechado.
2. Avaliar largura/posição da área clicável e `SafeArea` frente
   às regiões reservadas à navegação/gestos Android.
3. Repetir teste físico no A55 com ADB autorizado, capturando
   somente presença/ausência do Ghost e sem copiar notificações.
4. Decidir explicitamente com Master se a permissão de notificações
   habilitada é desejada; não revogar nem conceder unilateralmente.


## Correção proposta em 17/10/2026 — execução bloqueada

- Na largura compacta, o trilho invisível foi movido para
  `right: 20` e reduzido a `width: 42`, afastado do
  gesto da borda Android.
- Adicionado botão explícito `ghost-open-button` com semântica
  «Abrir Ghost por botão», `right: 24` e 44×44 dp.
- Novo teste de widget para classe A55 412×915: abre e fecha
  Ghost sem deslizar a borda.
- Não foi possível rodar `flutter analyze`, testes ou build:
  GitHub Codespaces devolveu **HTTP 402 (billing issue)** ao
  iniciar o ambiente anterior. Esse impedimento é externo ao código.
- Estado desta alteração: **PATCH AVALIDAR**. Não declarar
  correção física PASS; nenhum APK desta revisão foi instalado.
