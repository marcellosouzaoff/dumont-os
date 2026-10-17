# ADR-0022 — Pré-validação do Galaxy A55 (sem instalação)

**Data:** 17/10/2026
**Branch:** `feature/flutter-visual-prototype-20261017`
**Alvo:** Galaxy A55 SM-A556E / Android 16 / API 36

## Objetivo e limites

Preparar validação física de celular para DUMONT Flutter sem root,
sem modificar HOME, Knox, bootloader, configurações de segurança
ou aplicativos já existentes. O pacote alvo é exclusivamente
`br.com.dumont.experience.preview.trial`.

## Inventário observado por Remote Desktop Commander (A55)

- Termux acessível; ADB instalado: 1.0.41 / android-tools 37.0.0p1.
- `adb devices -l`: nenhum dispositivo autorizado. Estado **BLOCKED**
  para instalação e teste via ADB.
- `getprop init.svc.adbd`: `running`; USB configurado como
  `sec_charging,adb`; `ro.adb.secure=1`.
  Esses indicadores não fornecem acesso ADB automaticamente.
- `getprop service.adb.tls.port`, `service.adb.tcp.port` e
  `persist.adb.tcp.port`: vazios. Sem sessão de depuração sem fio
  verificável. Pairing requer ação explícita na tela Android.
- `/system/bin/cmd package list packages -u` não listou nomes DUMONT.
  **UNKNOWN se os aplicativos estão instalados**: o acesso do Termux
  aos pacotes pode ser filtrado pelo Android; não usar ausência da
  saída como prova absoluta de desinstalação.
- A chamada `/system/bin/pm` falhou ao resolver `cmd` pelo PATH
  do Termux; `/system/bin/cmd` responde à consulta de leitura.
  Não tentar escalar privilégios ou modificar PATH de segurança.

## Inventário de APKs encontrados nos Downloads do A55

Arquivo | packageName | versão declarada
--- | --- | ---
`DUMONT-v0.4.0-dev.apk` | `br.com.dumont.launcher` | 0.4.0-dev
`DUMONT-Preview-v0.4.0-dev.apk` | `br.com.dumont.launcher.preview` | 0.4.0-dev-preview
`DUMONT-Pilot-v0.4.2-stable-dev.apk` | `br.com.dumont.launcher.pilot` | 0.4.2-dev-pilot
`~/dumont-test/dumont-debug.apk` | `br.com.dumont.launcher` | 0.4.0-dev

Observações comprovadas por `aapt dump badging`, mas a existência de
APKs em Downloads **não prova** que tenham sido instalados.

O Pilot antigo exibe no `apksigner verify --print-certs`:
`7898a5f8177537852038b78de14115a4a3f1ef3cfc9bf9dd7174c56b413beda7`.
A assinatura do APK Flutter trial de Fase 6 foi:
`c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46`.
As assinaturas e nomes de pacote são diferentes. Nunca tratar a trial
como atualização do Pilot nem tentar substituir as versões antigas.

## Condições para a próxima execução

1. Usuário abre **Configurações > Opções do desenvolvedor >
   Depuração sem fio > Parear dispositivo usando código** no A55.
2. Obter endereço/porta de pareamento e código temporário da tela,
   sem registrar código em relatório, Git ou Drive.
3. `adb pair HOST:PORT` e `adb connect HOST:PORTA_DE_DEPURACAO`,
   somente depois que o usuário disponibilizar os dados corretos.
4. Verificar `adb shell getprop ro.product.model` = `SM-A556E`,
   Android 16, arquitetura arm64-v8a, pacotes atuais e política de
   assinatura antes de qualquer instalação.
5. APK novo deve ser o da Fase 6 com SHA-256
   `869386ef846ddb991b789cea47faabca7b58b7e9174e87c83e688b02338a0ead`;
   pacote `br.com.dumont.experience.preview.trial`, assinatura
   `c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46`.
6. Instalação normal apenas da trial e com consentimento explícito
   se o Android solicitar confirmação.
7. Teste físico: orientação atual do celular, dock, catálogo, Ghost,
   abrir/fechar janelas e retorno das configurações públicas.
8. Nunca inferir teste A55 a partir de dimensões simuladas pelo Flutter.

## Estado de testes

- A55: identificação e verificação dos APKs locais **PASS**.
- ADB autorizado no A55: **BLOCKED** (depuração sem fio não pareada).
- Instalação e teste de interface Flutter no A55: **UNKNOWN**.
- DeX real no tablet: **UNKNOWN**, não ativado nesta rodada.
- Tablet SM-X510 e branch original permanecem intactos.
