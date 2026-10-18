# DUMONT OS — Homologação e preservação P0

Data: 18/10/2026. Alvo exclusivo: Galaxy Tab S9 FE (SM-X510), Android 16.

## PASS confirmados

- Remote Desktop Commander e ADB conectados ao SM-X510.
- HOME Samsung preservada: `com.sec.android.app.launcher/.activities.LauncherActivity`.
- Prévia Flutter `br.com.dumont.experience.preview.ghostlabr39170437504a1` instalada paralelamente às versões `.trial` e `.ghostlabr39148517148a1`.
- Assinatura APK v2 aprovada pelo `apksigner` oficial via Ubuntu Java 21.
- Commit `1f2e56ff866e5333a40d4befc8f6ff7f4cb74a17`, Flutter CI run `39170437504` e Android run `39170437515`: SUCCESS.
- Ghost físico: abrir painel / ler informações reais / localizar `Fechar Ghost` / fechar, PASS no teste anterior no tablet.
- Dock: seis rótulos de acessibilidade presentes em código e testes Flutter CI aprovados; a captura física anterior mostra seis ícones e catálogo Kotlin.
- Backup P0 integral local em `/sdcard/Download/DUMONT_P0_INTEGRAL_2026-10-18_376d427.tar.gz`, 12.974.501 bytes; SHA-256 `569b0ada08b12f64041d86897d6768f82c8422991a5001b644548cbad6919cf6`: PASS.
- Clone original `~/dumont-publish`: HEAD `376d42704b2fd5c3e448d26635c62a0979687c9d`, dez alterações preparadas, sem modificações na rodada.
- Artefato APK Flutter recente preservado no Dropbox: `/DUMONT/DUMONT_FLUTTER_DOCK_1F2E56F_RUN39170437504.zip` (não é backup P0 integral).

## UNKNOWN / PENDENTE

- Leitura física dos seis rótulos Dock: UNKNOWN; o foco do tablet voltou ao ChatGPT/Chrome durante a automação. Não atribuir falha ao aplicativo.
- Paisagem e DeX físicos: UNKNOWN. Testes simulados de breakpoints, janelas e orientação constam no CI; nenhum ajuste forçado da rotação do tablet foi aplicado.
- Transferência do backup **binário integral** P0 ao Dropbox/Drive: PENDENTE. Verificação da pasta Dropbox não encontrou o arquivo.
- A conexão `rclone` foi iniciada com OAuth no navegador, mas expirou sem aprovação (`exit 2`). Nenhuma credencial foi armazenada e nenhum envio foi confirmado.
- A ferramenta `rclone v1.74.3` foi instalada no Termux; script `~/dumont-validation/p0-preservation/backup-p0-dropbox.sh` validou integridade P0 e bloqueio sem remote configurado. Para enviar, é necessária aprovação OAuth pelo usuário no navegador.

## Regras de preservação

A55 suspenso e não acessado. Nenhum merge em `main`, nenhuma exclusão de APK antigo, nenhuma alteração na HOME Samsung, nenhum reset ou perda das dez alterações preparadas. Não declarar backup remoto PASS sem confirmação da cópia e hash.
