# ADR-0018 — AndroidBridge em tempo real: bateria e conectividade

**Data:** 17/10/2026
**Projeto:** DUMONT OS Flutter Experience
**Branch:** `feature/flutter-visual-prototype-20261017`

## Escopo

Atualizar o estado do dispositivo sem agendadores e sem consultas
periódicas à API Android. Preservar a arquitetura Flutter + Kotlin,
HOME Samsung, Knox, privacidade, custo zero e o pacote de teste `.trial`.

## Contrato

- `MethodChannel("br.com.dumont.experience/native")` fornece a consulta
  inicial `getDeviceStatus` (fallback somente de leitura).
- `EventChannel("br.com.dumont.experience/device_events")` entrega mapas
  com `batteryPercent`, `isCharging`, `networkType`,
  `model`, `androidVersion`.
- Kotlin registra `ACTION_BATTERY_CHANGED` e
  `ConnectivityManager.registerDefaultNetworkCallback` somente quando
  o Flutter se inscreve; `onCancel` e `onDestroy` liberam ambos.
- Eventos são postados na thread principal via `Handler`, com
  deduplicação de snapshots idênticos. Não há loops, polling, timers,
  serviços em background ou rede externa.
- O Flutter observa via `StreamSubscription` e cancela no
  `dispose`. A resposta da consulta inicial não sobrescreve eventos
  mais recentes (proteção contra condição de corrida).
- O EventChannel é ativado automaticamente somente no Android real;
  a prévia web e testes Flutter ficam sem chamadas inválidas.
- Um stream pode ser injetado nos testes para provar atualização
  da interface e cancelamento sem serviço Kotlin real.
- A conexão é apenas tipo de transporte. Sem SSID, localização,
  identificador persistente, conteúdo de notificações ou dados privados.
- A única permissão relacionada é `ACCESS_NETWORK_STATE`, normal,
  declarada previamente. Nenhuma permissão especial concedida.

## TDD / resultados

- RED: os testes iniciais não compilaram porque o serviço de eventos
  não existia.
- Primeiro GREEN de contrato: 2/2 testes direcionados PASS.
- A suíte completa identificou exceções em hosts sem EventChannel.
  Implementada verificação de plataforma e injeção de stream nos testes.
- Flutter 3.47.7: `flutter analyze` **PASS / zero problemas**;
  `flutter test` **PASS / 27 de 27 testes**.
- Kotlin/Android ARM64: **aguardando compilação**.
- Teste físico de transição de bateria ou rede no Tab S9 FE:
  **UNKNOWN** antes da validação.
