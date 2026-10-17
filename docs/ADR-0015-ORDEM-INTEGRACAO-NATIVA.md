# ADR-0015 — Sequência de integração nativa aprovada

**Data:** 17/10/2026
**Produto:** DUMONT OS — Flutter Experience
**Regra:** a instalação no tablet é a ÚLTIMA atividade de validação.

## Ordem obrigatória

1. **Ícones reais dos aplicativos:** Android PackageManager fornece
   os Drawable das atividades MAIN/LAUNCHER; Kotlin converte para PNG
   em miniatura limitada. Flutter renderiza bytes válidos usando
   Image.memory e usa ícone genérico quando indisponível.
2. **Bateria, conectividade e dispositivo:** Kotlin produz snapshot
   somente de leitura de bateria, carregamento, transporte da rede ativa,
   modelo e Android. Sem SSID, localização ou identificadores únicos.
   Apenas ACCESS_NETWORK_STATE (permissão Android normal);
   estados indisponíveis permanecem UNKNOWN.
3. **Configurações públicas:** allowlist de IDs e intents oficiais Android.
   Apenas abre telas, sem alterar preferências e sem APIs internas Samsung.
4. **Notificações:** BLOQUEADO até autorização explícita do usuário.
   Não declarar NotificationListenerService, não pedir permissão especial,
   não ler conteúdo, não habilitar listener. Na interface informar
   claramente o estado SEM ACESSO / aguardando autorização.
5. **Por último — APK e tablet:** validar assinatura e compatibilidade,
   instalar uma prévia isolada só após gates anteriores e conferir
   funcionamento real em SM-X510 / DeX. NÃO substituir HOME ou Pilot.

## Verificações da etapa em código

- Canal Flutter/Kotlin `br.com.dumont.experience/native`:
  `listApps` (+`iconPng`), `launchApp`, `getDeviceStatus`,
  `openSettings`.
- Ações de configurações são restritas pela allowlist Dart **e**
  Kotlin. Pacote isolado `br.com.dumont.experience.preview`.
- RED: contrato novo falhou antes da implementação do codec.
- GREEN: `flutter analyze` **0 problemas**, `flutter test` **19/19 PASS**,
  incluindo imagens de referência regeneradas pelo renderizador Flutter.
- Build Kotlin/Android ARM64 debug: **PASS**, Flutter 3.47.7,
  Android SDK 36 e NDK 28.2.
- APK SHA-256: `96c972e0580da7f55b8c9e70404f49cd9caa46229c0caeb34327760353d91283`.
- Copiado ao Termux do tablet em `~/dumont-validation/artifacts/
  dumont-native-integrations-20261017-arm64-debug.apk`, hash conferido.
- Instalação no tablet: **NÃO REALIZADA**.
