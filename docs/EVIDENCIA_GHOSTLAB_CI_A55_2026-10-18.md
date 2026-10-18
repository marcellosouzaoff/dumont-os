# DUMONT OS — Evidência de instalação isolada Flutter no Galaxy A55

**Data:** 18/10/2026
**Dispositivo:** Galaxy A55 / SM-A556E / Android 16
**Escopo:** prévia Flutter de desenvolvimento; nenhuma alteração de HOME ou ROM.
**Base:** branch `feature/flutter-visual-prototype-20261017`, commit `f9b2dbc997c40fab322c1c6514f752097ee97151`.

## CI (evidências públicas)

- Flutter: [run 39146621801](https://github.com/marcellosouzaoff/dumont-os/actions/runs/39146621801) — **PASS**.
- Android: [run 39146621929](https://github.com/marcellosouzaoff/dumont-os/actions/runs/39146621929) — **PASS**.
- Flutter CI: `flutter analyze`, `flutter test`, teste de identidade Ghost Lab 5/5, build APK e teste do `applicationId`: PASS.
- APK artefato: `dumont-flutter-ghostlab-debug` (retention 3 dias).
- SHA-256 APK: `c9998966a269cbe71f2125848d561d3af0a1719c5e9d370cb5d5ef79c30f884c`.

## Assinatura — precaução

- Fingerprint extraído do certificado v2/v3 do novo APK: `0a8b57753da3b9a7792d8e0a023a2828f571f295a4c105acdc39687e4225f66c`.
- Fingerprint da instalação antiga `.ghostlab`: `e7da4725973a4b1f58609183d051caa5a2db0957fc0346f666c4f7afbf5d56b6`.
- Os certificados diferem. A extração do fingerprint não substitui a verificação criptográfica integral `apksigner`.
- Mitigação: o CI usa `GITHUB_RUN_ID` + `GITHUB_RUN_ATTEMPT` para obter um `applicationId` único por execução/tentativa.
- Pacote novo: `br.com.dumont.experience.preview.ghostlabr39146621801a1`.

## Testes físicos no A55

| Verificação | Estado |
| --- | --- |
| APK novo, sem `-r`, instalação `adb install --no-streaming` | PASS — `Success` |
| `pm path` localizou pacote novo | PASS |
| `.trial` anterior permaneceu instalado | PASS |
| `.ghostlab` anterior permaneceu instalado | PASS |
| HOME padrão `com.sec.android.app.launcher` | PASS |
| Abrir aplicação via `monkey` | PASS |
| `MainActivity` nova identificada como `topResumedActivity` | PASS |
| Abrir Ghost via botão encontrado por UIAutomator | PASS |
| Painel `Ghost` visível após toque | PASS |
| Fechar Ghost por botão / gesto | UNKNOWN — ainda não comprovado |
| Screenshots, DeX, teste físico no tablet com APK novo | UNKNOWN |

Foram realizados testes sem desinstalação, substituição, alteração de launcher padrão, limpeza de dados ou concessão de permissões privilegiadas.
O ADB wireless no Termux pode precisar de `adb nodaemon server`, pois a inicialização automática do daemon foi recusada ocasionalmente pelo Android.

## Pendências

1. Confirmar fechamento do Ghost e teste de acessibilidade completo.
2. Examinar prévia visual nos dois fatores de forma; instalar variante isolada no tablet apenas após conexão estável.
3. Estabelecer identidade de assinatura persistente para atualizações planejadas. A configuração `release` atual usa chave debug, portanto **não é homologada para distribuição**.
4. Enviar e conferir o SHA-256 do backup binário P0 externo. A restauração local no tablet já foi testada.

**Critério:** PASS/FAIL/UNKNOWN conforme evidência; build bem-sucedido não equivale a homologação de experiência, segurança ou release.
