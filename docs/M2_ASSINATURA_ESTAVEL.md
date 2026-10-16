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

## Validação real da assinatura estável — Tab S9 FE

**Data:** 16/10/2026 · **Aparelho:** SM-X510 · **Resultado:** PASS (escopo de desenvolvimento, não produção)

| Etapa | Evidência / resultado |
|---|---|
| Preparação de ferramentas | `apksigner` e OpenJDK 21 instalados no Termux; assinatura verificada com `apksigner verify` |
| Chave de desenvolvimento | Keystore local `~/.dumont-private-signing/dumont-debug-signing.p12`, fora do GitHub, permissão 600, pasta 700 |
| Certificado local | SHA-256 `7898a5f8177537852038b78de14115a4a3f1ef3cfc9bf9dd7174c56b413beda7` |
| Pilot v2 | Build [38901143209](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38901143209) (PASS), commit `941ea85`, versionCode 2 |
| APK v2 assinado localmente | SHA-256 `f94ab44bbb6014733664527b9a7c410c63035e6e9072336224d52f0b7a8f68eb` |
| Instalação inicial v2 | `adb install -t` retornou `Success`; abertura da MainActivity retornou `Status: ok` |
| Persistência preparada | Favorito de teste incluído pela interface; arquivo de preferências passou a conter 9 favoritos |
| Pilot v3 | Build [38901670655](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38901670655) (PASS), commit `0de3a62`, versionCode 3 |
| APK v3 assinado localmente | SHA-256 `74cdfa49779aa9170b26255efc593cce3770f6c14fc2913940092ea7f863f88c` |
| Atualização v2 → v3 | `adb install -r -t` retornou `Success` sem desinstalar o aplicativo |
| Dados preservados | SHA-256 do XML local antes e depois: `56e51831ef6e3c7a2bdf2057decf5961a0440e647336404a3062727198adf7f1` |
| Reabertura v3 | `Status: ok`; versão instalada confirmada com `dumpsys package` |
| Preservação dos outros apps | `br.com.dumont.launcher`, `.preview` e `.pilot` presentes simultaneamente |
| HOME original | `cmd role get-role-holders android.app.role.HOME` retornou `com.sec.android.app.launcher` |

**Nota de segurança:** removida a cópia transitória **sem criptografia** da chave privada (`private-key.pem`) após validar a keystore PKCS#12. Foram preservados somente a keystore, o certificado público e o arquivo de senha na área privada do Termux. A chave privada **não foi enviada ao GitHub**.

**Limite essencial:** o primeiro APK original continua com certificado distinto (`9feb2917…06270892f`) e não pode ser atualizado usando esta chave nova. A compatibilidade de atualização foi comprovada **somente para `br.com.dumont.launcher.pilot`**.

**Próximo gate:** fazer backup cifrado independente da keystore e da senha antes de adotar o Pilot como caminho definitivo. Ainda não há validação de produção, de HOME padrão ou de instalação no Galaxy A55.
