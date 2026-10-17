# M2 — Fundação Android do DUMONT

**Estado histórico da fundação M2.** O conteúdo a seguir descreve
o início da implementação. Em 17/10/2026 existem um Kotlin Pilot v1.1
instalado historicamente no Tab S9 FE, código v1.2 compilado/testado
e um protótipo Flutter isolado com testes PASS, ainda não instalado.
Consulte `STATUS_2026-10-17.md` para evidência atualizada.

## Escopo entregue neste commit

- APK Android convencional em Kotlin + Jetpack Compose; candidato HOME mediante escolha explícita do usuário.
- Topo escrito somente **DUMONT**; status bar e segurança continuam sob controle Android.
- Área central com busca local, grade de aplicativos e opção inicial Google / Todos.
- Dock inferior com atalhos persistidos localmente; toque longo em aplicativos para fixar ou remover.
- Ícones obtidos exclusivamente de aplicativos instalados por meio do PackageManager.
- Layout adaptativo para A55 retrato e S9 FE paisagem; bordas, paleta e espaçamentos do Design System v0.3.
- Modo claro e escuro acompanhando o sistema operacional.
- Nenhuma permissão perigosa, acesso a armazenamento, root, telemetria ou serviço de acessibilidade.

## Compatibilidade de dependências

A Compose BOM 2026.09.00 do protótipo inicial foi corrigida para 2026.06.00:
as versões Compose 1.12+ requerem compileSdk 37 e Android Gradle Plugin 9.1.1+,
enquanto esta fundação fixa compileSdk 36 e AGP 8.13.2. Reavaliar a atualização
com build validado antes de adotar AGP 9.

## Limitações intencionais

- A janela central é um painel próprio DUMONT; não há janelas de apps terceiros.
- O filtro Google altera somente os ícones mostrados no launcher, não remove ou desativa apps Samsung.
- Ícones, nomes e disponibilidade dependem dos pacotes efetivamente instalados.
- Os dados de segurança/Knox não são inventados nem exibidos como se tivessem sido consultados.
- Fontes DM Sans e Cormorant Garamond constam da especificação, mas **ainda não foram incorporadas** por falta de pacote de distribuição licenciado.
- Variações de tela, desempenho, acessibilidade e comportamento HOME em One UI permanecem **UNKNOWN** até teste real.

## Ambiente de build

- Gradle 8.13 (wrapper oficial), Android Gradle Plugin 8.13.2, Kotlin / Compose Compiler 2.3.20.
- Compose BOM 2026.06.00 (Compose 1.11; compatible with compileSdk 36), Android compileSdk/targetSdk 36, minSdk 28, JDK 17.
- Projeto: `./gradlew :app:assembleDebug :app:testDebugUnitTest`.
- Pipeline de validação em `.github/workflows/android.yml` após autorização explícita do escopo GitHub `workflow`. Nenhum envio automático a lojas, sem instalação por ADB.

## Gates para instalar no Tab S9 FE ou A55

1. CI Android **PASS** e artefato APK gerado sem erro.
2. Revisão de permissões do AndroidManifest.xml.
3. Teste em aparelho mediante autorização explícita, sem mudar launcher padrão automaticamente.
4. Escolha do launcher por diálogo/configuração do próprio Android.
5. Teste de reversão para a tela inicial Samsung.
6. Testes visuais de recorte, teclado, rotação, Dock, pesquisa, desempenho e acessibilidade.

**Estado esperado antes do CI:** código criado = PASS; build APK = UNKNOWN; execução em ambos aparelhos = UNKNOWN.

## Assinatura e convivência de APKs — gate de testes

- A atualização de um APK anterior foi recusada no tablet com `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. O Android corretamente impediu substituir uma instalação assinada por outra chave, preservando seus dados.
- A assinatura de depuração gerada automaticamente no runner GitHub é **efêmera**, não adequada a atualizações de APK entre diferentes execuções.
- Até uma identidade de assinatura estável e privada ser configurada, o build `debug` utiliza o pacote **`br.com.dumont.launcher.preview`** e o rótulo **DUMONT Preview**, coexistindo com `br.com.dumont.launcher`.
- O aplicativo original e seus favoritos **não devem ser removidos** automaticamente. O Preview não assume a HOME padrão.
- Chaves privadas nunca entram no repositório público. A tentativa de configurar um segredo protegido no GitHub não foi concluída; **assinatura estável = UNKNOWN/BLOCKED**.
- Não distribuir nem publicar builds `release` até aprovação explícita de estratégia de assinatura, identidade de pacote e ciclo de atualizações.
