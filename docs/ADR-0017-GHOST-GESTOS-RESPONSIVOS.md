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
