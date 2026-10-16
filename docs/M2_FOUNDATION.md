# M2 — Fundação Android do DUMONT

**Status:** implementação inicial em branch de desenvolvimento; validação de compilação e testes obrigatória antes de qualquer instalação.

## Escopo entregue neste commit

- APK Android convencional em Kotlin + Jetpack Compose; candidato HOME mediante escolha explícita do usuário.
- Topo escrito somente **DUMONT**; status bar e segurança continuam sob controle Android.
- Área central com busca local, grade de aplicativos e opção inicial Google / Todos.
- Dock inferior com atalhos persistidos localmente; toque longo em aplicativos para fixar ou remover.
- Ícones obtidos exclusivamente de aplicativos instalados por meio do PackageManager.
- Layout adaptativo para A55 retrato e S9 FE paisagem; bordas, paleta e espaçamentos do Design System v0.3.
- Modo claro e escuro acompanhando o sistema operacional.
- Nenhuma permissão perigosa, acesso a armazenamento, root, telemetria ou serviço de acessibilidade.

## Limitações intencionais

- A janela central é um painel próprio DUMONT; não há janelas de apps terceiros.
- O filtro Google altera somente os ícones mostrados no launcher, não remove ou desativa apps Samsung.
- Ícones, nomes e disponibilidade dependem dos pacotes efetivamente instalados.
- Os dados de segurança/Knox não são inventados nem exibidos como se tivessem sido consultados.
- Fontes DM Sans e Cormorant Garamond constam da especificação, mas **ainda não foram incorporadas** por falta de pacote de distribuição licenciado.
- Variações de tela, desempenho, acessibilidade e comportamento HOME em One UI permanecem **UNKNOWN** até teste real.

## Ambiente de build

- Gradle 8.13 (wrapper oficial), Android Gradle Plugin 8.13.2, Kotlin / Compose Compiler 2.3.20.
- Compose BOM 2026.09.00, Android compileSdk/targetSdk 36, minSdk 28, JDK 17.
- Projeto: `./gradlew :app:assembleDebug :app:testDebugUnitTest`.
- Modelo de CI em `ci/android.yml.example`; ativação bloqueada até autorizar escopo GitHub `workflow`. Nenhum envio automático a lojas, sem instalação por ADB.

## Gates para instalar no Tab S9 FE ou A55

1. CI Android **PASS** e artefato APK gerado sem erro.
2. Revisão de permissões do AndroidManifest.xml.
3. Teste em aparelho mediante autorização explícita, sem mudar launcher padrão automaticamente.
4. Escolha do launcher por diálogo/configuração do próprio Android.
5. Teste de reversão para a tela inicial Samsung.
6. Testes visuais de recorte, teclado, rotação, Dock, pesquisa, desempenho e acessibilidade.

**Estado esperado antes do CI:** código criado = PASS; build APK = UNKNOWN; execução em ambos aparelhos = UNKNOWN.
