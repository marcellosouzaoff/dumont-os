# DUMONT — Validação física do botão Ghost Lab no Galaxy A55

**Data:** 18/10/2026
**Estado:** PASS para o ciclo de abertura e fechamento por botão no aparelho real.
**Escopo:** variante isolada br.com.dumont.experience.preview.ghostlab, não gesto da borda.

## Ambiente
- Galaxy A55 / SM-A556E, Android 16; ADB sem fio autorizado.
- Pacotes Ghost Lab e preview.trial instalados simultaneamente.
- HOME padrão Samsung com.sec.android.app.launcher preservada.

## Procedimento e observações
1. A Activity Ghost Lab foi aberta pelo comando ADB am start -W, que retornou Status: ok.
2. Toque na área do botão interno em coordenadas físicas (945,315).
3. Ghost Lab continuou topResumedActivity e a árvore UIAutomator apresentou 1 ocorrência da semântica Ghost.
4. Toque no controle de fechar em coordenadas físicas (925,328).
5. Ghost Lab continuou topResumedActivity. A árvore UIAutomator passou a apresentar 0 ocorrências de Ghost e 1 ocorrência de Abrir Ghost.
6. Duração observada do ciclo controlado: 6,65 segundos.
7. A evidência foi limitada a contagens de elementos; nenhum texto de mensagens ou notificações foi recolhido. Capturas XML temporárias foram apagadas do armazenamento compartilhado após verificação.

## Limites e garantias
- PASS: abertura/fechamento do Ghost por botão, em aparelho físico A55.
- UNKNOWN: gesto da borda, ainda não validado; não extrapolar o PASS.
- UNKNOWN: revisão visual por pixels/cores no dispositivo.
- Sem instalação, desinstalação, concessão de permissões, mudança de HOME ou alterações de Knox.
- CI Flutter da variante Ghost Lab havia concluído com sucesso, execução 39119066825.
