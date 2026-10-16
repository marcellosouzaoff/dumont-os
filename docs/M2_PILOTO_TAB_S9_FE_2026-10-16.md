# M2 — Piloto experimental no Galaxy Tab S9 FE

**Data:** 16/10/2026 · **Dispositivo:** Samsung Galaxy Tab S9 FE (SM-X510) · **Estado:** PILOTO_PARCIAL_PASS

## Escopo e evidência de build

- Repositório: `marcellosouzaoff/dumont-os`, branch `feat/m2-android-foundation`.
- Código compilado: commit `edc7b1cc8ef9d743e90b7c952311b7149dfa73a7`.
- GitHub Actions: execução [38844392175](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38844392175), conclusão **success** para `assembleDebug`, `testDebugUnitTest` e publicação do artefato.
- APK de depuração, SHA-256: `81a144b3f7ebab061c5d1fa7c2864523297e0bb5a2d7cdad23e8026cc6eb688d`.
- Instalado por ADB após autorização explícita do proprietário. Nenhuma operação de flash, root, unlock, alteração de Knox ou seleção automática de launcher padrão foi executada.

## Testes executados remotamente

| ID | Observação verificável | Resultado |
|---|---|---|
| TAB-01 | Tablet `SM-X510` identificado pelo ADB | PASS |
| TAB-02 | `adb install -r -t` retornou `Success` e PackageManager lista `br.com.dumont.launcher` | PASS |
| TAB-03 | `am start -W -n br.com.dumont.launcher/.MainActivity` retornou `Status: ok`; processo observado | PASS |
| TAB-04 | Captura do aplicativo real mostrou barra DUMONT, painel central, aplicativos Google e Dock inferior | PASS |
| TAB-05 | Filtro inicial Google mostrou 18 atividades; opção Todos mostrou 62; retorno ao filtro inicial | PASS |
| TAB-06 | Busca local “Maps” mostrou 1 atividade; “YouTube” mostrou 2; ao limpar a busca, retornou a 18 | PASS |
| TAB-07 | Toque no Chrome do Dock trouxe a atividade Chrome para a frente; DUMONT foi reaberto | PASS |
| TAB-08 | `cmd role get-role-holders android.app.role.HOME` continuou retornando `com.sec.android.app.launcher` antes e após o teste | PASS |
| TAB-09 | Botão HOME do Android preservou o launcher Samsung; APK DUMONT continuou instalado e reabriu | PASS |
| TAB-10 | Nenhum erro fatal de DUMONT encontrado na amostra consultada do logcat | PASS (amostragem, não garantia de ausência futura) |
| TAB-11 | Toque longo adicionou o app Google Flow ao Dock (9 favoritos); após reiniciar a atividade o estado persistiu; toque longo removeu o favorito e restaurou os 8 iniciais | PASS |

## Condições específicas

- O tablet estava operando com janelas livres/ambiente de desktop; o DUMONT abriu numa janela de aplicativo. Este teste **não valida** a experiência de tela cheia como launcher padrão.
- Um `BACK` durante o teste retornou à atividade anterior; foi possível abrir o DUMONT novamente. Não foi evidenciada falha fatal nesse evento.
- A captura contém contexto de outros aplicativos e, por privacidade, foi mantida apenas no ambiente local de testes do tablet; **não publicada** no repositório público.
- Os números 18 e 62 representam atividades de lançamento visíveis, não necessariamente 18 e 62 pacotes únicos.

## Pendências / UNKNOWN

- Nenhum teste físico executado no **Galaxy A55 (SM-A556E)**.
- Definir DUMONT como HOME padrão e depois retornar à Samsung: **não testado**; preservar a escolha atual até etapa aprovada.
- Gestos, rotação, orientação fixa, teclado físico, acessibilidade/TalkBack, mudança de escala de fonte, consumo de bateria e estabilidade prolongada: **UNKNOWN**.
- Teste básico de adição/remoção e persistência de favoritos: **PASS** (TAB-11); reordenação e acessibilidade do gesto longo permanecem **UNKNOWN**.
- App icon exibido na moldura da janela ainda é genérico do Android; precisa de identidade própria DUMONT.
- Fontes DM Sans/Cormorant Garamond ainda não estão incorporadas ao APK.
- A captura sugere que a grade central exige rolagem em dimensões menores de janela. Ajustar ergonomia após ensaio em tela cheia e com tamanho de fonte aumentado.

## Próximos gates

1. Ajustes visuais e acessibilidade no modo de janela livre.
2. Ampliar testes de favoritos: reordenação, limites e acessibilidade (adição/persistência/remoção básica já validada).
3. Validar instalação e inicialização no A55 mediante autorização específica.
4. Somente após aprovação separada, ensaiar DUMONT como HOME padrão e demonstrar reversão para One UI.

**Conclusão:** instalação, abertura, busca, Dock, persistência básica de favoritos e preservação da HOME Samsung foram observados em aparelho real. Não declarar conclusão do MVP, validação dos dois aparelhos, disponibilidade para distribuição pública ou aprovação da substituição da HOME.

## Teste adicional: DUMONT Preview (sem substituir a instalação original)

**Build:** commit `1a1b35f`; execução [38898851075](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38898851075) — compilação, testes unitários e artefato **PASS**.

- Pacote de depuração independente: `br.com.dumont.launcher.preview`.
- SHA-256 do APK: `7aa671eae633a7e4ec17ea3523b74aff58c4bd5986446bbc8a61673172083404`.
- Instalação ADB retornou `Success`; PackageManager confirmou presença simultânea de `br.com.dumont.launcher.preview` e `br.com.dumont.launcher`.
- Abertura explícita `br.com.dumont.launcher.preview/br.com.dumont.launcher.MainActivity`: `Status: ok` e processo observado — **PASS**.
- Hierarquia UI confirmou `DUMONT`, `Aplicativos`, `Buscar aplicativos`, `Configurar Home` e `Google · 18` — **PASS**.
- Busca `Maps`: `Google · 1` e ação `Limpar` presentes — **PASS**. Acionar `Limpar` restaurou `Google · 18` — **PASS**.
- O launcher Samsung continuou como detentor de `android.app.role.HOME` — **PASS**.
- Tentativa de atualização do pacote original usando outro APK debug retornou `INSTALL_FAILED_UPDATE_INCOMPATIBLE` — **FAIL esperado por assinatura diferente**, sem desinstalar nem perder favoritos. A variante Preview separada elimina a necessidade de remoção do app original, mas não substitui uma solução estável de assinatura.
- **Tela cheia:** a aplicação continuou em modo `freeform` no ambiente desktop do tablet; execução como HOME real em tela cheia não foi validada — **UNKNOWN**.

Capturas reais de tela permaneceram somente no ambiente privado do tablet e não foram publicadas no GitHub.

## Atualização adicional: assinatura estável Pilot

O teste de atualização local v2 → v3 foi concluído com `adb install -r`, usando a mesma keystore privada no Termux, sem perda de favoritos e sem alterar a HOME Samsung. Evidências e limites: [M2 — Assinatura estável](M2_ASSINATURA_ESTAVEL.md).

## Piloto v0.5 — instalação autorizada e verificação no Tab S9 FE

**Data:** 16/10/2026. **Commit-fonte:** `e3578c8`. **CI:** [38910639316](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38910639316) — PASS. **Dispositivo:** SM-X510.

| Controle | Evidência | Resultado |
|---|---|---|
| APK v0.5 assinado e verificado | SHA-256 `4f9512ae405e98a55f1f4dd6f31c864559a3417b1f3a8353b228ea4195c4fdfc`; certificado de desenvolvimento SHA-256 `7898a5f8177537852038b78de14115a4a3f1ef3cfc9bf9dd7174c56b413beda7` | PASS |
| Upgrade Pilot | `adb install -r -t` retornou `Success` | PASS |
| Versão instalada | `versionCode=4` | PASS |
| Preferências intactas | Antes/depois SHA-256 `5979a7c91ff6bcc9fd7a8e0802e4fa7605963d36e8ad535dc9531bfbf9e0cce9` | PASS |
| Abertura do Pilot | `am start -W` retornou `Status: ok`, início frio ~2,08 segundos (um ensaio, não média) | PASS |
| Workspace inicial | Hierarquia mostrou `DUMONT`, `Aplicativos`, `Atualizar`, botões `Apps` e `Termux`, sem painel de busca aberto | PASS |
| Catálogo via barra superior | Toque no menu exibiu `Buscar aplicativos`, `Todos`, `Atualizar`, `Fechar` e `Google · 18` | PASS |
| Alternar Todos | Resultado inconclusivo: atividade de ChatGPT assumiu o foco antes da leitura subsequente | UNKNOWN |
| Fechar painel | Não verificado por foco externo da tela | UNKNOWN |
| Termux pelo Dock | Ícone e rótulo presentes, mas execução pelo toque não confirmada | UNKNOWN |
| HOME padrão | `com.sec.android.app.launcher` permaneceu no papel HOME | PASS |
| DUMONT original | Pacote `br.com.dumont.launcher` permaneceu instalado | PASS |
| Consumo de bateria/ANR | Sem protocolo de medição longa; nenhum erro fatal do Pilot no recorte final de logs | UNKNOWN para desempenho/bateria |

**Observação:** após verificar a primeira abertura do painel, o usuário/ambiente voltou a exibir o aplicativo ChatGPT em primeiro plano. Os testes subsequentes de interface não foram forçados novamente para evitar interferência na sessão do proprietário.

**Estado final:** Pilot v0.5 instalado, preferências preservadas, One UI configurada como HOME. Aguardando revisão visual do usuário para aprovar ou priorizar ajustes; nenhuma troca de HOME ou limpeza foi executada.

## DUMONT v0.6 — Layout solicitado, build e instalação sem ativar HOME

**Data:** 16/10/2026. **Commit:** `06a3d9d`. **Build CI:** [38915796996](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38915796996) — PASS: arquitetura estática, build/testes e upload do APK.

| Critério | Evidência | Resultado |
|---|---|---|
| Barra superior reorganizada e barras flutuantes | Revisão de Compose implementou Aplicativos / Configurações Android / Terminal à esquerda e DUMONT à direita | PASS código / UNKNOWN visual |
| Papel de parede completo | Wallpaper preenchendo Box raiz; barras aplicadas sobre wallpaper, enableEdgeToEdge e safeDrawingPadding | PASS código / UNKNOWN visual |
| Transparência | 5–80% com configuração local, padrão 30%, painel aberto por DUMONT | PASS código / UNKNOWN interação |
| Menu Aplicativos | Primeiro estado mostra todos os componentes MAIN/LAUNCHER visíveis e filtráveis | PASS código / UNKNOWN interação |
| Dock inferior | Sem botão Apps e sem Termux/Google forçados; pins manuais + usos reais | PASS código / UNKNOWN interação |
| Estatísticas mais usadas | Contagem local por aberturas no DUMONT; estatísticas Android 7 dias somente após autorização especial de Acesso ao uso | PASS código / UNKNOWN permissão |
| CI | Run 38915796996, conclusão success | PASS |
| APK CI | SHA-256 `6510c0d3f6d289cc103e279b5ea93d96ca2f2c09fb107928b9ae299ee77f66e0` | PASS |
| APK assinado localmente | SHA-256 `53fe96064bfe9327ae4c23c285d08906e4ef6c24fea6704500fc45d9e6081c64`, certificado `7898a5f8177537852038b78de14115a4a3f1ef3cfc9bf9dd7174c56b413beda7` | PASS |
| Upgrade de Pilot | `adb install -r -t` retornou `Success`, versão instalada `versionCode=5` | PASS |
| Preferências antigas | Antes/depois hash SHA-256 `5979a7c91ff6bcc9fd7a8e0802e4fa7605963d36e8ad535dc9531bfbf9e0cce9`, backup local protegido | PASS |
| DUMONT original / HOME Samsung | `br.com.dumont.launcher` preservado; role HOME ainda `com.sec.android.app.launcher` | PASS |
| Tela do proprietário | ChatGPT em primeiro plano; nenhum novo teste de interação executado para evitar interrupção | UNKNOWN funcional em dispositivo |
| Galaxy A55 | Nenhuma instalação nem alteração nesta revisão | Não testado |

**Teste visual pendente:** proprietário abrir o DUMONT Pilot v0.6, verificar desktop, menu superior, centralização do catálogo, botão Terminal, Configurações nativas, navegação de apps, fixação por toque longo, controles de transparência e persistência após reinício. Bateria e memória seguem sem baseline comparativo.

**Nota sobre uso de aplicativos:** autorização de Usage Access não foi dada via ADB; o `AppOps` permanece no modo padrão. Sem opt-in, ranking automático conta exclusivamente aberturas realizadas pelo DUMONT.

## v0.6.1 — Instalação e verificação física das barras alinhadas

**Data:** 16/10/2026. **CI:** [38919269533](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38919269533), PASS. **Commit do código:** `ea29ec338ff9b804e636ee443850a22c5fa86343`.

| Verificação | Evidência | Estado |
|---|---|---|
| APK testado | SHA-256 `f5af9e1efe06e1533fd1afef05ef543532c473fee7bcc8ad4fab04628332702b`; certificado SHA-256 `7898a5f8177537852038b78de14115a4a3f1ef3cfc9bf9dd7174c56b413beda7` | PASS |
| Atualização Pilot v0.6 para v0.6.1 | `adb install -r -t` → `Success`; Android indica `versionCode=6` | PASS |
| Segurança e reversibilidade | `com.sec.android.app.launcher` continua HOME; pacotes `br.com.dumont.launcher` e `.pilot` permanecem instalados | PASS |
| Preferências de favoritos | SHA-256 antes e depois `3d9c77953233187ff6b456ee1ffa3503761b401d99dbd41ac69336a86df1b9fe` | PASS |
| Histórico de abertura de apps | Arquivo local `dumont_local_launches.xml` idêntico antes/depois da atualização | PASS |
| TopBar e Dock | Captura ADB local privada conferida; ambos começam e terminam nas mesmas margens laterais | PASS visual, sem inspeção de pixel subjacente automatizada |
| Cor sólida | Imagem da tela exibe barras escuras opacas | PASS visual |
| Dock inicial | Ícones essenciais visíveis e centralizados (Chrome, Arquivos, Gmail, Agenda, Drive, Fotos, Play Store, WhatsApp) | PASS visual |
| Termux | Não aparece no Dock DUMONT; acesso permanece pelo menu superior Terminal | PASS visual/menu |
| Menus superiores | `Aplicativos`, `Configurações`, `Terminal` à esquerda e `DUMONT` à direita na árvore de interface | PASS |
| Ajustes de aparência | Toque na marca DUMONT abre painel `Aparência DUMONT`; mostra `Transparência das barras: 0%` e slider; Fechar restaura a área de trabalho | PASS para abertura/fechamento; persistência do slider não testada |
| Faixa branca observada | Barra de título branca pertencente à janela Samsung DeX, fora da superfície DUMONT; permanece no modo janela | Externo, não modificado |
| Consumo de bateria | Sem medição controlada Samsung vs DUMONT | UNKNOWN |

**Dados e backup:** cópias de preferências guardadas apenas no diretório privado Termux `dumont-validation/private/v061-rollback/`, sem upload. Tela Samsung/Knox/firmware/bootloader não alterados. App A55 não alterado.

**Pendente:** validar em telefone, alternância real da transparência e persistência, toque/abertura dos apps essenciais, layout em janela maximizada ou modo HOME, acessibilidade, desempenho e bateria.
