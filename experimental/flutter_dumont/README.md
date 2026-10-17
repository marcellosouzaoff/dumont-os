# DUMONT Flutter Experience — laboratório visual

**Versão experimental:** v0.1 (APK de build inicial com versionName 1.0.0).
**Estado:** preview v1.0.0 instalada no Tab S9 FE em 17/10; código da Fase 3 testado no Codespace, ainda NÃO instalado no tablet.
**Não é ROM, SystemUI nem launcher HOME.**

## Design aprovado

| Token | Cor | HEX | Uso |
|---|---|---|---|
| Baritone Blue | escuro | `#272836` | Desktop, TopBar, Dock e Ghost |
| Cloud Dancer | creme | `#F0EEE9` | Conteúdo e texto claro |
| Curds & Whey | acento | `#BCA483` | Elementos discretos |

Tipografia DM Sans para a interface, Cormorant Garamond para editorial.
Binários não versionados; licenças SIL OFL incluídas.

## Executar no Codespace / Linux

Pré-requisitos: Flutter SDK (validado com 3.47.7). Android SDK API 36
e Java 21 para APK. Navegador compatível para `flutter run -d chrome`.

~~~bash
cd experimental/flutter_dumont
bash scripts/setup_fonts.sh
flutter pub get
flutter analyze
flutter test
flutter run -d chrome
flutter build web --release
flutter build apk --debug --target-platform android-arm64
~~~

`flutter test --update-goldens` só quando houver alteração
intencional da UI; em CI/regressão usar `flutter test`.
O script prepara os assets de fonte sem precisar armazená-los no Git.

## O que está implementado

- Barra DUMONT, Dock, papel de parede vetorial, painel Aplicativos,
  pesquisa, filtro por categoria, grid responsivo e Ghost.
- Gesto na borda para abrir o Ghost; recolhido fica invisível.
- Golden renderizados pelo motor Flutter: desktop, tablet, celular
  e Ghost aberto, em `test/goldens/`.
- 11 testes Flutter passaram em 17/10 (incluindo 6 novos); golden desktop e Ghost atualizados.
- Builds Web release e Android ARM64 debug passaram.

## O que NÃO está implementado

- Apps reais do tablet, ícones reais e abertura de apps externos.
- Serviço de notificações Android, permissões, uso ou wallpapers reais.
- Bridge `MethodChannel`/`EventChannel` para Kotlin e testes DeX.
- Instalação física do novo build Fase 3 no Tab S9 FE e qualquer preview no Galaxy A55.

O catálogo atual é demonstrativo. Nunca apresentar suas informações
como dados observados no aparelho.

## Segurança e pacotes

Pacote Android de teste: `br.com.dumont.experience.preview`.
O Kotlin Pilot segue isolado em `br.com.dumont.launcher.pilot`;
o original tem `br.com.dumont.launcher`. Não substituir nenhum deles
nem a HOME Samsung. Knox e firmware intactos.

Mais detalhes no [status técnico](../../docs/STATUS_2026-10-17.md)
e [ADR-0012](../../docs/ADR-0012-FLUTTER-VISUAL-PROTOTYPE.md).

## Fase 3 — em implementação

A Paleta 01 passa a ser centralizada em `lib/theme/dumont_theme.dart`,
componente de status em `lib/widgets/dumont_state_badge.dart` e gerenciamento
de **janelas internas** em `lib/windows/dumont_window_manager.dart`.
O botão **Ajustes** abre a janela de demonstração Linus Security com Knox
marcado `UNKNOWN`. Essas janelas são internas ao Flutter e não operam janelas
DeX ou aplicativos externos. `flutter analyze` e `flutter test`
(11/11) PASS no Codespace. Novo APK ARM64 debug: PASS (SHA-256 `ce7a1835cf43c860b4a166df936fdbbc20e285d3c13822d3bd2b7ce3c878dac8`).
CI automático e instalação/teste físico Fase 3 permanecem pendentes.

## Ponte Android nativa — etapa 1

O pacote Flutter Preview agora possui um `MethodChannel` (Kotlin)
para listar atividades `MAIN/LAUNCHER` exportadas e abrir somente
componentes novamente verificados pelo Android. O catálogo mostra
apps reais quando o canal responde; no navegador mantém a demonstração.
A grade nativa ainda usa **ícones genéricos**. Não há integrações de
notificações, Knox, SystemUI, papel de parede ou controle de janelas DeX.
A integração nova ainda requer instalação e testes físicos no SM-X510.

Build ARM64 debug com AndroidBridge Kotlin: **PASS** no Codespace,
SHA-256 `fe3d629c3991563703fc1f839b256fad19df123b5e17d00cb78d4cee4eaac47c`. O pacote ainda não foi
instalado nem testado fisicamente.


## Integrações nativas — ordem aprovada

A prévia usa ícones de apps via `PackageManager`, informações de
bateria e conexão sem identificadores privados, e apenas abre
configurações públicas Android. Os estados desconhecidos nunca simulam
rede conectada ou bateria cheia. A área de notificações continua
sem listener nem permissão e aguarda autorização explícita.

**A instalação e os testes no tablet são o último gate.**
Consulte `docs/ADR-0015-ORDEM-INTEGRACAO-NATIVA.md`.


## Notificações — ativação voluntária

A opção Notificações do Ghost reconhece o estado de acesso especial.
Somente ao tocar sem acesso concedido ela abre a página oficial de
permissões Android. O sistema pede confirmação MANUAL. Mesmo após
autorização, o DUMONT guarda apenas nome do aplicativo e timestamp
em memória (até 12 registros); não lê nem exibe títulos, mensagens,
códigos ou ações. Ao revogar o acesso, a UI limpa os registros
disponíveis. O sistema Android pode conceder alcance mais amplo ao
listener, por isso o usuário deve avaliar cuidadosamente o aviso
oficial. Sem envio de dados para terceiros.


## Prévia no Tab S9 FE — 17/10/2026

A versão debug usa `applicationIdSuffix = ".trial"`, portanto instala
`br.com.dumont.experience.preview.trial` separadamente para evitar
incompatibilidade de assinaturas com a prévia antiga. O APK isolado foi
compilado e instalado, SHA-256 `f10a9367ca20b6e9a039dcada9f2cdc9482389cbdce56197787e10e2b100dced`.
Catálogo real e abertura de Chrome tiveram teste físico PASS.
A permissão especial de notificações continua não concedida.
O gesto lateral Ghost ainda exige verificação física manual.


## Ajuste de gestos Ghost — ADR-0017

O painel Ghost agora abre com gesto suave acumulado (-48dp) na borda
invisível de 62dp, ou com toque na mesma borda. A alteração preserva
o launcher Android e o estilo da Paleta 01. Consulte o ADR-0017.


## Atualização em tempo real — ADR-0018

`device_events` (Kotlin `EventChannel`) publica mudanças de bateria e
transporte da rede ativa com receptores Android sob demanda, sem polling.
O Flutter escuta apenas no Android real, faz consulta inicial e libera
a assinatura ao descartar a tela. Hosts sem Android continuam com
fallback demonstrativo seguro e nenhum listener de eventos ativado.


Validação tablet: APK `24f76f297b1a3531a77e720f5914d41ea455045bdc913f88f8ae8b0f6d18427f` compilado e instalado
no pacote separado `.trial`. Bateria, rede e modelo exibidos em Ghost;
recebimento de transição física espontânea ainda UNKNOWN.


## Retorno das Configurações — ADR-0019

O Flutter observa o ciclo de vida. Ao voltar de uma tela externa
(estado `resumed`), consulta novamente os dados nativos e o estado
de consentimento das notificações sem polling e sem alterá-lo.
Eventos mais recentes têm prioridade sobre respostas atrasadas.
Testes de regressão em `test/settings_resume_test.dart`.


Teste físico de 17/10/2026: Ghost → Internet (diálogo Android) e
Ghost → Visor (Activity Android) abriram e retornaram ao Flutter.
Versão de teste: `br.com.dumont.experience.preview.trial`.
Ver ADR-0019 para critérios e limites da validação.


## Janelas responsivas — ADR-0020

Janelas internas do Flutter agora podem ser maximizadas e restauradas
por botão acessível, com margens de 8dp. O estado maximizado acompanha
alterações do viewport e preserva a geometria anterior. Não gerencia
janelas do Android/DeX.


Validação física no Tab S9 FE: clique em «Maximizar» abriu a janela
na área útil; clique em «Restaurar» devolveu a geometria inicial.
Android trial somente, sem mudanças na HOME e sem teste DeX real.
