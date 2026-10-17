# ADR-0019 — Retorno das Configurações Android e proteção de estado

**Data:** 17/10/2026
**Branch:** `feature/flutter-visual-prototype-20261017`
**Âmbito:** Flutter/Dart, sem novas permissões Kotlin.

## Diagnóstico

A consulta `_refreshDeviceStatus()` era aplicada apenas com
`_deviceInfo == null`. Se o usuário entrasse nas Configurações Android
e voltasse, o DUMONT poderia continuar mostrando bateria/conectividade
anterior, especialmente se o EventChannel não entregasse um evento
naquele intervalo. Reinstalar ou resetar o aplicativo não é solução.

## Implementação

- `DumontDesktop` implementa `WidgetsBindingObserver` e remove o
  registro em `dispose`.
- `didChangeAppLifecycleState(resumed)` solicita novo estado nativo
  (`getDeviceStatus`) com `force: true` e nova fotografia de
  permissões de notificações, **apenas leitura**.
- Um contador marca a última versão recebida no `EventChannel`; uma
  solicitação de leitura registra a versão existente quando começou.
  Se o contador mudou, a leitura não sobrescreve o evento mais novo.
- Outro contador identifica a consulta mais recente e impede que
  respostas de consultas anteriores substituam o valor atual.
- Fora de Android, exceções `MissingPluginException` continuam
  protegidas e a prévia usa sinalização UNKNOWN.
- Abertura de configurações usa somente os intents públicos permitidos
  no contrato Kotlin existente; nenhum toggle ou alteração de sistema
  é executado pelo DUMONT.

## TDD

1. RED comprovado: depois de voltar das configurações, bateria
   simulada mudou 54% → 72%, mas UI não atualizou.
2. RED comprovado: ao retomar o aplicativo, não ocorreu nova leitura
   (`statusReads` ficou em 1), impedindo a proteção contra leitura
   desatualizada.
3. GREEN: ambos os testes passaram, incluindo o caso de resposta
   atrasada (75%) não sobrescrever evento mais recente (91%).
4. `flutter analyze`: **PASS — zero problemas**.
5. Suíte `flutter test`: **PASS — 29/29**.
6. Build Android ARM64, comparação de certificado e teste físico:
   **UNKNOWN** até a respectiva execução nesta rodada.

## Auditoria não destrutiva dos pacotes

- Galaxy Tab S9 FE, modelo SM-X510: ADB conectado.
- `pm list packages -u` e `pm path` não localizam o pacote
  `br.com.dumont.experience.preview`.
- `br.com.dumont.experience.preview.trial` está instalado; Android
  mostra `lastUpdateTime=2026-10-17 15:36:51`.
- A prévia original havia sido observada em rodada anterior, mas
  **a causa e a data da ausência atual continuam UNKNOWN**.
- Nenhuma desinstalação, limpeza de dados, root, flash, concessão de
  permissão especial ou reinstalação de pacote original nesta rodada.
- Antes de qualquer restauração é necessária autorização específica.
