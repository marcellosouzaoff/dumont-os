# Assinatura local estável — DUMONT Pilot (M2)

**Estado:** preparação experimental para atualizações de testes; não é assinatura de produção.

## Regra de proteção

- O APK original `br.com.dumont.launcher` e o APK antigo `br.com.dumont.launcher.preview` permanecem intactos.
- Nenhuma chave privada original foi recuperada: copiar um APK ou seu certificado público não permite assinar atualizações com sua identidade.
- O novo app de teste terá identificador **`br.com.dumont.launcher.pilot`**, rótulo **DUMONT Pilot**, versão de teste inicialmente `0.4.1-dev-pilot` e `versionCode` 2; a atualização de validação usará `0.4.2-dev-pilot` e `versionCode` 3.
- A compilação GitHub Actions possui uma assinatura de depuração temporária; para atualizar o Pilot posteriormente, re-assinar cada build **localmente**, com a mesma keystore protegida no Termux.
- Somente `scripts/sign-pilot-local.sh` e documentos públicos entram no repositório; arquivos `.p12`, senhas e chaves nunca entram em commit, ZIP público, log ou pasta Downloads.

## Fluxo de atualização

1. Confirmar GitHub Actions PASS e baixar o artefato CI do commit aprovado.
2. Instalar `apksigner` no Termux e executar `scripts/sign-pilot-local.sh <ci.apk> <pilot-assinado.apk>`.
3. Confirmar certificado SHA-256 com `apksigner verify --print-certs`. Comparar com a impressão digital pública esperada.
4. Instalar no tablet usando ADB sem tornar o Pilot HOME padrão. Uma versão posterior deve incrementar `versionCode` e usar a mesma keystore, para `adb install -r`.
5. Confirmar que tanto o DUMONT original quanto seus favoritos estão preservados.

## Riscos e limitações

- Sem a keystore original do primeiro APK, não é possível atualizá-lo diretamente.
- A chave de desenvolvimento foi criada no tablet, fora do GitHub. Antes de dependermos dela em versões futuras, será necessário fazer **backup cifrado independente**, em local aprovado pelo proprietário.
- Este método não autoriza publicar APK de produção nem substituir o launcher Samsung.
- Perda da chave estável significa que os aplicativos que ela assina não podem receber atualização normal sem migração.
