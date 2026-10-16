# ADR-0006 — Integração DUMONT × Samsung One UI

**Data:** 16/10/2026 · **Implementação inicial:** DUMONT Pilot 0.8.0-dev · **Estado:** aguardando CI e verificação física.

## Escopo real da integração

O DUMONT funciona como um **launcher Android alternativo**. Os aplicativos, serviços e recursos existentes da Samsung/Android continuam sendo os originais. Não há clonagem de One UI, replicação de ROM, root, dispositivo administrado, APIs Knox privilegiadas ou substituição de SystemUI.

### Fase 1 — Galaxy Bridge (implementação desta versão)

- Um botão **Galaxy** na barra superior abre o painel Integração Galaxy; os menus Aplicativos, Configurações e Terminal permanecem.
- Aplicativos Samsung aparecem no painel somente se o Android anunciar uma atividade MAIN/LAUNCHER acessível e instalada no perfil. Exemplos do inventário: Galeria, Câmera, Meus Arquivos, Contatos, Galaxy Store, Gravador de Voz, Modos e Rotinas. A lista difere conforme aparelho e versão do software.
- Controles de Wi-Fi, Internet, Bluetooth, VPN, tela, som, bateria, armazenamento, acessibilidade, privacidade, segurança, aplicativos, notificações e papel de parede abrem telas/painéis oficiais do Android por **intents públicos**. Em Samsung eles podem oferecer recursos específicos da One UI. Caso a tela direta não exista, abrir a tela geral de Configurações com aviso.
- Nenhum rádio ou permissão muda silenciosamente: o usuário confirma nas telas oficiais Android/Samsung.
- Android: declarar atividade redimensionável para uso com multi-janelas do DeX. Não desativar cabeçalho/barras oficiais do DeX.
- A HOME Samsung permanece no controle até autorização específica e testes completos. DUMONT Pilot continua com applicationId isolado; DUMONT original e apps da Play Store preservados.

### Fase 2 — Área de trabalho verdadeira (planejada, não implementada)

- Hospedagem de **widgets reais Samsung/Android** via AppWidgetHost/AppWidgetManager, com seleção e permissão de vinculação do usuário e persistência segura. Sem afirmar que qualquer widget Samsung é compatível até testar.
- Atalhos dinâmicos de aplicativos, links para funções rápidas, app shortcuts e acessibilidade por teclado/mouse.
- Layout de desktop com organização de pastas, ícones e arrastar/soltar.
- Ajustes de janelas redimensionáveis e comportamento no DeX, respeitando as regras de Android Multi-Window.
- Serviços de arquivos e compartilhamento por Storage Access Framework/Share Sheet, com acesso somente autorizado pelo usuário.

### Fase 3 — Limites técnicos

- Recursos protegidos: Knox, Samsung Pass, biometria, recursos internos de DeX, firmware, pacotes privilegiados, permissões exclusivas de plataforma e componentes privados da One UI **não são transplantáveis** para um launcher comum.
- APIs Knox restritas (por exemplo, gerenciamento e controle remoto) podem exigir Device Owner ou Profile Owner conforme Samsung, não apenas autorização de um app normal. DUMONT não converterá um dispositivo pessoal em aparelho gerenciado para superar essas restrições.
- Apps e widgets pertencentes a outro perfil Android podem ser invisíveis sem permissões e políticas apropriadas.
- O DUMONT não controla barra de status global, chamadas de emergência, bloqueio de tela, teclado do sistema, permissões dos outros apps, atualizações OTA ou painel Samsung fora da sua própria interface.

## Fontes técnicas

- Android Developer: **Host an app widget** — https://developer.android.com/develop/ui/views/appwidgets/host
- Samsung Developer: **Samsung DeX FAQ** — https://developer.samsung.com/samsung-dex/faq.html
- Samsung Knox Docs: **Restricted API methods** — https://docs.samsungknox.com/dev/knox-sdk/api-reference/restricted-api-methods/

## Testes e gates

| Item | Critério | Estado inicial |
|---|---|---|
| GAL-01 | Apps Samsung visíveis são encontrados via PackageManager, não uma lista inventada | Código revisado; teste físico pendente |
| GAL-02 | Galaxy abre e fecha painel, sem bloquear área de trabalho | UNKNOWN |
| GAL-03 | Wi-Fi/Internet, Bluetooth e configurações abrem tela oficial | UNKNOWN |
| GAL-04 | Galeria, Câmera, Meus Arquivos abrem somente se launchable | UNKNOWN |
| GAL-05 | Volta da tela Samsung ao DUMONT | UNKNOWN |
| GAL-06 | Códigos e nomes de ações restritas não estão presentes | Testes de unidade pendentes |
| GAL-07 | HOME permanece Samsung e dados não são alterados | UNKNOWN |
| GAL-08 | UI se adapta ao modo retrato A55 e janela Samsung DeX | UNKNOWN |
| GAL-09 | Acesso a widgets e recursos proprietários | NÃO IMPLEMENTADO — backlog explícito |
| BUILD | Static validator, Gradle/unit tests, APK assinatura estável | UNKNOWN |

**Regra de aceite:** CI PASS sozinho não comprova integração física; apontar separadamente o que foi observado e o que segue UNKNOWN.

## Auditoria de integração e CI — 16/10/2026

- Inventário direto por ADB do Galaxy Tab S9 FE SM-X510: pacotes Samsung de câmera, galeria, contatos, Galaxy Store, rotinas, serviços de DeX e Knox foram localizados. Isto prova presença, não prova que todos tenham atividades inicializáveis.
- O PackageManager do tablet resolveu as ações públicas Android Wi-Fi, Bluetooth, tela, som, economia de bateria, armazenamento, acessibilidade, privacidade, segurança, aplicativos, painel de Internet, notificações e seleção do papel de parede.
- CI da primeira tentativa no commit f0e844d: FAIL de compilação por duas constantes de Settings não existentes no SDK público. Corrigidas no commit fa320a69bad0dd0796cf0b4089654ab54c7c5f38: notificações usam intent de configuração resolvido no aparelho, e wallpaper usa Intent.ACTION_SET_WALLPAPER.
- GitHub Actions corrigido 38925387660: PASS na validação estática, compilação Gradle, testes unitários e publicação do APK.
- APK CI SHA-256: ed50b6714290a4e675a257bff86d7583639c9da5e317dbe52273a4669f82079e.
- APK Pilot v0.8 assinado localmente SHA-256: 4d9b2d6bf5ba2e88fd24433fd264e80529f2381e8f4a988f0aabb4fec060a692. Certificado público SHA-256: 7898a5f8177537852038b78de14115a4a3f1ef3cfc9bf9dd7174c56b413beda7. Verificação v3 PASS.
- Não houve instalação nesta etapa: Galaxy Tab ainda com br.com.dumont.launcher.pilot versionCode 7 (v0.7); Samsung com.sec.android.app.launcher permanece HOME.
- Nenhuma permissão privilegiada concedida, política Knox alterada, aplicativo removido, perfil de administração ativado ou componente One UI desabilitado.
- Itens GAL-01 a GAL-05 e GAL-08 seguem UNKNOWN em interação real; precisam de instalação aprovada e teste de abrir/voltar. Widgets Samsung, atalhos avançados e funcionamento como HOME DUMONT permanecem fora do escopo executado.
- No A55, a visibilidade de pacotes no Termux continua limitada pelo Android; integração ainda não instalada nem testada nele.
