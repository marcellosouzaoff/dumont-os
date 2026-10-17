# ADR-0017 — Ghost: gesto cumulativo e acionamento acessível

**Data:** 17/10/2026
**Branch:** `feature/flutter-visual-prototype-20261017`
**Escopo:** Flutter interno; sem mudanças em HOME, ROM, DeX ou privilégios.

## Defeito identificado

O painel Ghost abria somente quando o `primaryDelta` de **um único**
evento `onHorizontalDragUpdate` fosse menor que -8 px. Um gesto Android
real frequentemente chega em eventos sucessivos menores, impedindo
a abertura. Área invisível da borda tinha apenas 26 pixels lógicos
e podia disputar o gesto com navegação do Android.

## Solução

- Gesto soma o deslocamento horizontal desde `onHorizontalDragStart`.
- Ao alcançar **-48 pixels lógicos**, abre Ghost; não exige salto
  brusco entre frames nem múltiplas tentativas.
- Área invisível tem 62 pixels lógicos, permitindo iniciar o gesto
  longe da zona reservada pelo Android. Sem trilho visível ou mudança
  estética de marca.
- Toque na borda abre Ghost como alternativa acessível.
- `Semantics` anuncia «Abrir painel Ghost».
- Na abertura, atualiza em modo somente leitura o estado de acesso às
  notificações. Não concede permissão nem lê mensagens sem autorização
  especial manual.

## Evidências e pendências

- RED: dois testes novos falharam no código antigo (deslize suave e
  toque na borda).
- GREEN: testes direcionados **2/2 PASS**, Flutter analyze **0 issues**,
  suite Flutter **25/25 PASS** no Codespace.
- Teste físico tablet/ADB desta correção: **UNKNOWN** antes do novo APK.
- Notificações especiais continuam sem autorização Android; nenhum
  auto-grant ou acesso a conteúdos particulares.


## Validação física após correção

- Build Flutter ARM64 debug: **PASS**; APK SHA-256:
  `b04ed78df8f26517c9ce7f6beacf76346569bc37406901fa840ed2ef8b85740c`.
- Certificado de assinatura SHA-256:
  `c3fce7028c74f85ce9a77b2dc3f04cc44b6f7cfa3d0009e8a1545b47e9403a46`
  (igual ao `.trial` já instalado).
- Instalação `adb install -r` do pacote isolado
  `br.com.dumont.experience.preview.trial`: **PASS**.
- Prévia antiga permanece com lastUpdateTime 12:50:09, intacta.
- ADB UI hierarchy no SM-X510 (paisagem) delimitou a zona de interação
  Ghost em X=2196–2304 (pixels físicos). Deslize iniciado em X=2230,
  Y=720 com deslocamento gradual até X=1690: **PASS**.
- Teste de toque em X=2240, Y=730 após fechar Ghost: **PASS**.
- Ghost exibiu rede Wi-Fi, modelo SM-X510, Android 16, bateria real e
  «Acesso não concedido» para notificações.
- A tentativa anterior em X=2190 começou fora da zona X=2196–2304,
  justificando resultado falso negativo naquele teste físico.
- Nenhuma permissão especial concedida. Janelas DeX continuam de
  responsabilidade do Android.
