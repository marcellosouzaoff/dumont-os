# DUMONT Flutter Experience — Laboratório visual

Status: protótipo. Não é ROM, SystemUI ou launcher padrão.
Paleta 01: Baritone Blue #272836, Cloud Dancer #F0EEE9, Curds & Whey #BCA483.
Tipografia: DM Sans e Cormorant Garamond.

## Executar
Requer Flutter SDK. Para Android, configurar Android SDK e Java 21.

    bash scripts/setup_fonts.sh
    flutter pub get
    flutter analyze
    flutter test
    flutter run -d chrome
    flutter build web --release
    flutter build apk --debug --target-platform android-arm64

As fontes de código aberto e licenças OFL são obtidas das fontes oficiais pelo script; os arquivos binários não serão versionados no repositório.

## Primeira entrega
Janela Aplicativos, busca, filtros, marca DUMONT, Dock, fundo vetorial, responsividade e Ghost totalmente invisível quando fechado.
Todos os aplicativos nesta prévia são ilustrativos. Os recursos reais de Android (instalação, intents, notificações, widgets) ainda requerem integração Kotlin por Platform Channels.

## Testes
Flutter widget/golden: desktop 1440x900, tablet 1000x700, celular 390x844, Ghost e filtro/busca.
A imagem de teste é produzida pelo renderizador Flutter e não por gerador de imagens.

## Segurança
Mantenha o app separado de br.com.dumont.launcher e br.com.dumont.launcher.pilot.
Nunca alterar HOME/One UI/Knox silenciosamente, e não realizar deploy em A55 ou Tab S9 FE sem instalação expressamente solicitada.
