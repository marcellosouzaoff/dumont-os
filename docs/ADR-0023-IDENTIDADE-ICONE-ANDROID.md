# ADR-0023 — Ícone do aplicativo DUMONT (prévia Flutter)

**Data:** 17/10/2026
**Estado:** APROVADO PARA USO NA PRÉVIA / não equivale à homologação final do Brand Kit.

## Decisão

O Master escolheu a terceira imagem apresentada no processo de curadoria
visual: ícone quadrado de cantos arredondados em Baritone Blue,
monograma abstrato em Cloud Dancer com curva Burgundy, acompanhado
por wordmark DUMONT em composição de apresentação.

A imagem de referência enviada nesta conversa (`1000180047.png`)
foi usada como origem visual. **O ícone do Android usa somente
o símbolo quadrado**, sem wordmark ou cenário: o texto da marca não
seria legível no launcher nas densidades pequenas.

## Tratamento da imagem

- Recorte de composição de referência: x=477..940, y=182..645,
  imagem original 1536×1152.
- Raster base redimensionado a 1024×1024 px com Lanczos, máscara de
  cantos arredondados; base de cor Baritone Blue `#272836`.
- Densidades clássicas do Android:
  - `mipmap-mdpi/ic_launcher.png`: 48×48
  - `mipmap-hdpi/ic_launcher.png`: 72×72
  - `mipmap-xhdpi/ic_launcher.png`: 96×96
  - `mipmap-xxhdpi/ic_launcher.png`: 144×144
  - `mipmap-xxxhdpi/ic_launcher.png`: 192×192
- O AndroidManifest existente já usa `@mipmap/ic_launcher`.
  Nenhuma alteração de packageName, assinatura, permissões ou HOME.
- O sistema de ícones adaptativos/máscaras monocromáticas Android 13+
  ainda precisa ser desenhado com um vetor próprio: não declarar
  a imagem raster como ícone adaptativo ou monocromático pronto.
- Não alegar correspondência exata com Pantone a partir de amostragem
  de uma arte renderizada. Os valores são referências digitais aprovadas
  para a prévia, não medições cromáticas da imagem.

## Rastreabilidade e recuperação

Pacote contendo base 1024 e ícones de todas as densidades salvo no
Google Drive: `DUMONT_ANDROID_ICONS_v0.1.zip`, pasta DUMONT.
Pacote de transferência rastreável:
`DUMONT_ANDROID_MIPMAPS_v0.1.zip`, SHA-256
`931e91dcc2d1243dea6f31297c5f2a7b824c4e34ea66a66d1b0a7d1402da2ff5`.

Os PNGs da prévia Flutter são rastreados pelo Git. Os arquivos
anteriores podem ser restaurados por Git, sem alteração da branch
principal. **Nada foi instalado no Galaxy A55** nesta fase.

## Validação requerida

1. Conferir dimensões dos 5 PNGs e carga como arquivo PNG válido.
2. Testar o build/manifest Android da branch experimental.
3. Renderizar ícone no launcher em dispositivo real e validar legibilidade.
4. Obter consentimento antes da instalação no A55; a aprovação da
   identidade visual não autoriza automaticamente a instalação.


## Validação de compilação e tablet — rodada seguinte

- Flutter 3.47.7: `flutter analyze` **PASS**, zero problemas;
  `flutter test` **PASS**, 33/33.
- Build `flutter build apk --debug --target-platform android-arm64`
  **PASS**. APK SHA-256:
  `9e466de26c86c6ea718ed63186803f1209ede8d15fc111ef844d33f62ff3c301`.
- `aapt list` confirmou os cinco recursos `res/mipmap-*-v4/ic_launcher.png`
  dentro do APK resultante: **PASS**.
- Identificador do aplicativo: `br.com.dumont.experience.preview.trial`;
  certificado SHA-256:
  `c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46`;
  corresponde à assinatura da trial previamente instalada.
- Cópia local do APK no Termux do tablet verificada pelo SHA-256:
  **PASS**. APK local:
  `~/dumont-validation/artifacts/dumont-logo-961ab61-arm64-debug.apk`.
- No Tab S9 FE SM-X510: `adb install -r` **Success**,
  `am start -W` **Status ok**, UIAutomator confirmou DUMONT e
  Aplicativos. Instalado apenas pacote `.trial`.
- Android HOME permanece `com.sec.android.app.launcher`.
  Privilégio de notificações não foi concedido.
- **Avaliação visual da aparência do ícone na grade do launcher:
  UNKNOWN.** `aapt` comprova presença dos recursos, mas não como
  o launcher Samsung os compõe com máscara e escala.
- Galaxy A55: nenhum APK instalado nesta rodada; aprovação para
  instalar no A55 deve ser específica.
