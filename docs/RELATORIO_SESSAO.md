# Relatório de sessão — DUMONT v0.3

**Data de referência:** 15/10/2026
**Estado:** documentação pré-código.

## Decisões

- Nome de produto: DUMONT (repositório dumont-os).
- Dispositivos-alvo: Galaxy A55 SM-A556E e Tab S9 FE SM-X510.
- Android 16 preservado. Knox permanece intacto; interface de segurança chamada Linus Security.
- Barras superior e Dock inferior; painel/pasta de apps central; sem sidebar lateral fixa.
- Ícones de apps Google instalados; sem Finder nem ícones Samsung próprios.
- Paleta Cloud Dancer #F1EEE7, branco #FFFFFF, cinza-chumbo #4E5353, preto #252525, Mocha Mousse #A47864 e vermelho #CD212A.
- Tipografias DM Sans (interface) e Cormorant Garamond (editorial).
- Licenciamento de código/documentação original Apache 2.0; marcas e imagens fora do escopo.

## Publicação

Repositório público de destino: https://github.com/marcellosouzaoff/dumont-os

## Próximas validações

Conferir árvore pública, licenças de imagens e README; fechar gate pré-código; iniciar estrutura Android somente após autorização. Não foram executadas mudanças de ROM, firmware ou Knox.


## 17/10/2026 — Avanço Fase 3 Flutter

- Fonte: branch `feature/flutter-visual-prototype-20261017` no GitHub.
- Trabalho: preservada correção visual do tablet, tema Paleta 01 centralizado,
  badge PASS/WARNING/ERROR/UNKNOWN e janelas internas Flutter com foco,
  movimento, redimensionamento e fechamento. Exemplo: Linus Security
  com Knox UNKNOWN.
- Testes: Flutter 3.47.7, `flutter analyze` PASS 0 problemas e `flutter test`
  PASS 11/11 no Codespace. Dois golden regenerados pela alteração de layout.
- Instalação: pacote preview v1.0.0 confirmado por ADB no SM-X510.
  Código Fase 3 não instalado; build Android ARM64 debug PASS após
  preparação de Android SDK API 36 e NDK 28.2 no Codespace.
  APK de aproximadamente 75 MB, SHA-256: `ce7a1835cf43c860b4a166df936fdbbc20e285d3c13822d3bd2b7ce3c878dac8`.
- Publicação GitHub: commit de implementação `8131e72`; atualização
  de goldens e documentação em commit subsequente.
- Restrição: GitHub bloqueou a publicação de workflow por falta de escopo.
  O CI Flutter permanece PENDENTE.
- Próximas etapas: preparar Android SDK, compilar preview assinado em
  pacote isolado, validar janela responsiva no tablet, depois AndroidBridge.
- Sem troca de HOME, permissões sensíveis, root, flash, merge de produção
  ou impacto em OSAMBA.

### Encerramento técnico da rodada

- Build ARM64 debug PASS, SHA-256 conferido e APK armazenado somente no
  Termux do tablet para teste posterior, sem instalação.
- O arquivo de workflow Flutter foi preservado como template em
  `docs/ci/flutter-workflow-template.yml`; ativação exige escopo/permissão
  do GitHub para workflows, não disponível nesta sessão.
- Branch `main` e HOME Samsung permanecem intactas.


## 17/10/2026 — Integração Flutter/Kotlin — etapa 1

- Gap identificado: Flutter mostrava apps sintéticos, embora a base
  Kotlin já tivesse catálogo e capacidade de abertura via Android.
- Nova ponte `br.com.dumont.experience/native` com `listApps` e
  `launchApp` usando atividades públicas `MAIN/LAUNCHER`.
- `<queries>` de escopo restrito no manifesto, sem
  `QUERY_ALL_PACKAGES` nem permissões privilegiadas.
- Segurança: abertura exige revalidação da activity no catálogo.
- TDD: novo teste falhou sem serviço Dart (RED); após implementação
  `flutter analyze` 0 problems e `flutter test` **14/14 PASS**.
- Teste de UI revelou e corrigiu overflow de 1,5 px na categoria
  `Instalados`.
- Build Kotlin/Android ARM64 debug **PASS** (Codespace), SHA-256:
  `fe3d629c3991563703fc1f839b256fad19df123b5e17d00cb78d4cee4eaac47c`.
- O APK novo permanece em isolamento e não foi instalado.
  Integração em hardware real no Galaxy Tab S9 FE: UNKNOWN.
- Ícones nativos reais, device status, notificações, ajustes Samsung
  e Android DeX seguem pendentes. Sem troca de HOME/firmware.
- Workflow Flutter automático continua pendente por escopo GitHub.


## 17/10/2026 — Sequência de integrações nativas (reordenada)

- Ordem definida: ícones Kotlin → bateria/rede/modelo →
  configurações públicas → notificações SOMENTE após autorização →
  assinatura, instalação e testes no Tab S9 FE como último passo.
- Código implementado para os três primeiros módulos.
- Teste inicial falhou (contrato ausente), então foram adicionados
  DTOs Dart, consultas Kotlin, interfaces Flutter e testes.
- Flutter: análise PASS (0 issues), 19 testes PASS; quatro goldens
  foram regenerados para representar dados desconhecidos sem valores
  fictícios e painel Ghost atualizado.
- NotificationListener, consentimento especial e instalação:
  NÃO REALIZADOS. Nenhuma permissão privilegiada.
- Compilação Android nativa aguarda registro do resultado separado.


### Fechamento de build — nova sequência

- Android SDK 36 e NDK 28.2, Flutter 3.47.7:
  `flutter build apk --debug --target-platform android-arm64`: PASS.
- Hash SHA-256 do APK resultante: `96c972e0580da7f55b8c9e70404f49cd9caa46229c0caeb34327760353d91283`.
- APK de debug copiado para o Termux do SM-X510 e seu hash
  confirmado; **não instalado**.
- ADB confirma a prévia antiga versão 1.0.0, com data de instalação
  17/10/2026 12:50:09, sem alteração nesta rodada.
- `flutter analyze` PASS 0 issues; `flutter test` PASS 19/19.
- Permissão Android normal ACCESS_NETWORK_STATE usada apenas
  para o transporte da rede; notificações especiais sem autorização.
- Próximos gates: autorização explícita antes de acessar notificações;
  SOMENTE APÓS essas etapas, conferir assinatura e testar instalação
  no tablet, respeitando HOME/Knox/firmware Samsung.


## 17/10/2026 — Autorização para próxima etapa: notificações

- Usuário confirmou implementação das notificações seguindo a sequência
  estabelecida (autorização em conversa).
- Android ainda exige autorização especial manual por tela oficial;
  nenhuma concessão automática ou via ADB.
- Implementado listener privado com dados apenas em memória, limite
  de 12, campos `packageName`, `appLabel`, `postTimeMs`.
- UI Ghost exibe estado de acesso e abre a configuração oficial
  somente quando o usuário seleciona a função.
- Nenhum título/corpo/código ou histórico são persistidos.
- TDD, build, assinatura e teste físico aguardam validação;
  estado UNKNOWN até evidência.


### Encerramento — instalação isolada e validação física

- Flutter analyze PASS e flutter test 23/23 PASS.
- Novo APK compilado no Codespace: `f10a9367ca20b6e9a039dcada9f2cdc9482389cbdce56197787e10e2b100dced`.
- SHA-256 do certificado da antiga instalação: `dc9a66e6488f447216fde01d2b00a0ec97d6be6cc980a464dfa282eefe29844d`;
  novo: `c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46` (diferentes).
- Usado packageId `br.com.dumont.experience.preview.trial` em debug,
  sem atualizar/desinstalar o pacote antigo.
- Android ADB informou instalação `Success`, lançou MainActivity
  no primeiro plano e confirmou pacote original inalterado.
- Teste do catálogo Android: Chrome abriu por toque no DUMONT;
  Android Back retornou ao DUMONT.
- Indicador de bateria real exibido, listener especial Android
  não autorizado e não concedido.
- Teste físico do gesto Ghost inconclusivo; documentar UNKNOWN.
- Próximo gate: inspeção manual visual do Ghost e habilitação
  voluntária do acesso a notificações no Android, apenas caso o usuário
  aprove o aviso do sistema. Não reproduzir mensagens privadas.
- Relatório salvo no GitHub, Dropbox ainda sem upload.


## 17/10/2026 — Ajuste Ghost, antes do backup Dropbox

- Detectada causa provável de não abrir o Ghost: limiar `primaryDelta < -8`
  por evento; gestos suaves distribuem deslocamento em muitos eventos.
- Implementados limiar acumulado -48 dp, borda invisível 62 dp,
  abertura por toque, descrição para acessibilidade e atualização de
  estado nativo somente de leitura.
- Dois novos testes falharam antes do código (RED), e passaram depois.
- Flutter 3.47.7: `flutter analyze` PASS zero issues;
  `flutter test` PASS **25/25**, goldens mantidos.
- Ainda aguardam: compilação debug isolada, atualização do tablet,
  teste físico do novo gesto e upload Dropbox.

## Validação física após correção

- Build Flutter ARM64 debug: **PASS**; APK SHA-256:
  `b04ed78df8f26517c9ce7f6beacf76346569bc37406901fa840ed2ef8b85740c`.
- Certificado de assinatura SHA-256:
  `c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46`
  (igual ao `.trial` já instalado).
- Instalação `adb install -r` do pacote isolado
  `br.com.dumont.experience.preview.trial`: **PASS**.
- Prévia antiga permanece com lastUpdateTime 12:50:09, intacta.
- ADB UI hierarchy no SM-X510 (paisagem) delimitou a zona de interação
  Ghost em X=2196–2304 (pixels físicos). Deslize iniciado em X=2230,
  Y=720 com deslocamento gradual até X=1690: **PASS**.
- Teste de toque em X=2240, Y=730 após fechar Ghost: **PASS**.
- Ghost exibiu rede Wi-Fi, modelo SM-X510, Android 16, bateria real e
  «Acesso não concedido» para notificações.
- A tentativa anterior em X=2190 começou fora da zona X=2196–2304,
  justificando resultado falso negativo naquele teste físico.
- Nenhuma permissão especial concedida. Janelas DeX continuam de
  responsabilidade do Android.

### Backup da rodada e limite de Dropbox

- Branch de desenvolvimento e relatório verificados no GitHub.
- Arquivo de backup ZIP preparado a partir de `git archive`, sem
  arquivos não versionados, chaves ou dados privados do tablet.
- Conector Dropbox: conta acessível para leitura; não há pasta DUMONT.
  O método `upload_file` exige um arquivo anexado à conversa;
  não recebe caminho direto da instalação Termux.
- Compartilhamento externo pelo Termux foi bloqueado pela ferramenta.
  Não foi realizado nenhum upload, concessão de permissões ou
  tentativa de contornar esse bloqueio.
- Pendente: disponibilizar ZIP em Downloads do tablet e anexá-lo à
  conversa para transferência autorizada pelo conector Dropbox.


## 17/10/2026 — Fase 4: AndroidBridge em tempo real

- Escopo autorizado: monitorar mudanças do estado do dispositivo por
  eventos Android, evitando consultas periódicas.
- Contrato Kotlin/Flutter: `br.com.dumont.experience/device_events`;
  listener `ACTION_BATTERY_CHANGED` e callback da rede padrão.
- A conexão só existe enquanto o Flutter estiver inscrito; liberação
  de receptores em `onCancel`, integração com `dispose` do Flutter.
- Atualização de interface sem reinicializar o DUMONT; dados que
  não chegam continuam UNKNOWN em vez de fictícios.
- No primeiro ciclo TDD, teste falhou por método ausente (RED).
  Análise detectou erro de EventChannel em hosts sem Android;
  corrigido com ativação exclusiva em Android real e stream de teste.
- Suite Flutter **27/27 PASS**, analyze **zero issues**.
- Kotlin/Android build e validação física: ainda aguardam prova.
- Sem mudanças de HOME, Knox, firmware, instalação original,
  acessibilidade ou permissão de notificações especiais.
- Backup de rodada será atualizado após validação.


### Fechamento: build e tablet — AndroidBridge eventos

- Flutter: analyze **PASS 0 issues**, test **PASS 27/27**.
- Kotlin/Android: build APK ARM64 debug **PASS**; SHA-256 `24f76f297b1a3531a77e720f5914d41ea455045bdc913f88f8ae8b0f6d18427f`.
- Certificado de debug SHA-256 `c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46`, válido para a `.trial`.
- Transferência ao Termux com SHA-256 conferido; comando
  `adb install -r` **Success** para
  `br.com.dumont.experience.preview.trial`. Sem firmware, Knox, root.
- `am start`: MainActivity abriu em primeiro plano.
- UIAutomator do tablet: DUMONT e catálogo Android disponíveis;
  54% mostrado na barra e em Ghost (dumpsys battery também 54%),
  Wi-Fi, modelo SM-X510 e notificações «Acesso não concedido».
  Sem crashes observados no recorte do logcat examinado.
- Uma transição real de Wi-Fi ou nível da bateria **não foi provocada**
  nem observada nesta rodada. Testes simulados do EventChannel
  verificaram entrega de eventos e cancelamento no Flutter.
  Estado dessa verificação física: UNKNOWN.
- **Divergência relevante:** ao verificar pacotes Android após instalar
  o `.trial`, `br.com.dumont.experience.preview` original não
  constava mais em `pm list packages`, nem `pm path`.
  Na rodada anterior constava instalado. A origem da diferença é
  UNKNOWN; nenhum `adb uninstall`, limpeza de dados ou instrução
  para remover pacote foi executado nesta rodada. Não presumir causa
  e não reinstalar sem decisão do usuário.
- Acesso especial a notificações permanece não concedido;
  nenhuma ação automática de permissão foi realizada.
- Backup da rodada preparado por `git archive HEAD` e salvo na
  pasta Downloads do SM-X510, com comparação SHA-256 da cópia.
  Contém somente arquivos versionados; APK e dados privados do tablet
  não foram incluídos.
- Dropbox: o conector requer arquivo anexado à conversa e não aceita
  um caminho do Termux como origem. Upload REMOTO ainda **PENDENTE**;
  não afirmar sucesso nem enviar dados sem autorização da plataforma.
