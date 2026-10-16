# M2 — Auditoria de assinatura e continuidade de APK

**Data:** 16/10/2026
**Estado:** CERTIFICADOS_VERIFICADOS · CHAVE_ORIGINAL_NÃO_DISPONÍVEL
**Escopo:** apenas evidências públicas de assinatura e decisões futuras; nenhuma chave privada publicada

## APK original — cópia exata

O Galaxy Tab S9 FE (SM-X510) conserva a instalação original `br.com.dumont.launcher`.
O APK de `pm path` foi extraído via ADB para cópia privada no Termux e comparado, byte a byte, com o artefato original da compilação [38844392175](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38844392175).

| Propriedade | Resultado |
|---|---|
| APK instalado vs. artefato original | PASS: conteúdo idêntico |
| Tamanho | 29.048.571 bytes |
| SHA-256 do APK | `81a144b3f7ebab061c5d1fa7c2864523297e0bb5a2d7cdad23e8026cc6eb688d` |
| Certificado original | `CN=Android Debug, O=Android, C=US` |
| SHA-256 do certificado original | `9feb29172432f998514b5ad6c275faaf5e9160e0c2d4707cb41d83306270892f` |

Arquivos no **tablet**, fora do GitHub: `/sdcard/Download/DUMONT-original-backup-2026-10-16.apk` e `/sdcard/Download/DUMONT-original-public-certificate.der`. A cópia de segurança do APK não inclui dados internos do aplicativo; favoritos persistidos exigem backup separado e autorizado.

## Comparação com outras compilações

| Build | SHA-256 do certificado público |
|---|---|
| Original, 38844392175 | `9feb29172432f998514b5ad6c275faaf5e9160e0c2d4707cb41d83306270892f` |
| Atualização pré-Preview, 38897955511 | `75cbcfe9899bbb4d57af16b2049d31d25983d64c447b36d43888001d2100dcbd` |
| Preview isolado, 38898851075 | `20f0d3aeb0c6ce47abc77479c012907ea8dea444a39a3f55bd04c52838a2c308` |

Os certificados foram lidos dos blocos públicos **APK Signature Scheme v2**. Não foi extraída, descoberta ou reproduzida a chave privada original. A única saída do primeiro pipeline disponível nos artefatos do GitHub era o APK, não uma keystore.

## Consequência criptográfica

O certificado público de um APK pode ser copiado para fins de verificação, mas a **assinatura digital de uma compilação não pode ser transplantada** para outro APK com conteúdo diferente. Sem acesso à chave privada original (ou a uma rotação válida autorizada por ela), o Android recusa a substituição do mesmo `applicationId` por um APK assinado com outra chave.

Resultado já observado na tentativa de atualização: `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. Essa recusa é uma proteção Android legítima, não um problema a contornar.

## Decisão para os próximos builds

1. **Não desinstalar** nem zerar dados de `br.com.dumont.launcher` sem consentimento específico.
2. Manter `br.com.dumont.launcher.preview` como pacote separado de testes, sem migrar nem copiar dados internos de outros aplicativos.
3. Investigar se existe uma **keystore original**, com chave privada correspondente ao certificado `9feb2917...`. Nenhuma foi identificada entre os arquivos locais analisados; recuperação = **UNKNOWN**.
4. Para novas versões com atualização contínua, escolher uma **keystore estável própria**, guardar somente em armazenamento privado e configurar a assinatura de CI por um método autorizado. O certificado de uma keystore nova **não** permitirá atualizar a instalação original.
5. Caso a chave original continue indisponível, propor migração controlada com backup e restauração de preferências, após aprovação específica do proprietário, ou manter o pacote Preview e adotar um identificador definitivo novo.
6. Não inserir senhas, base64 de chaves, arquivos `.jks`, `.p12`, `.keystore` ou informações privadas de usuários no repositório público.

**Segurança:** `android.app.role.HOME` permaneceu com `com.sec.android.app.launcher`; o clone e a leitura da assinatura não envolveram root, flash nem modificações de Knox.
