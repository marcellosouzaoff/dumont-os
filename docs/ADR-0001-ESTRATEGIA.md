# ADR-0001 — Launcher Android, não ROM

**Projeto:** DUMONT v0.3
**Decisão:** aprovado em escopo documental.

## Contexto

Os dispositivos-alvo são Galaxy A55 (SM-A556E) e Tab S9 FE (SM-X510), com Android 16 / One UI 8.5, bootloaders bloqueados e Knox Warranty Bit 0 nas verificações registradas. Uma ROM exigiria alterações irreversíveis e validações ausentes.

## Decisão

A primeira implementação será um launcher Android reversível, escrito em Kotlin e Jetpack Compose, com barra superior DUMONT, catálogo/pasta central, Dock inferior, cores neutras e uso dos ícones reais dos aplicativos Google instalados.

## Limitações

O launcher não substitui o Android, firmware, SystemUI, drivers, Knox, autenticação ou gerenciamento de janelas de todos os aplicativos. A central Linus Security será informativa, com dados verificáveis e estados desconhecidos explícitos.

## Consequências

A solução permite distribuição por APK e retorno ao launcher Samsung nas configurações do Android. Root, desbloqueio OEM, flash, ROM e operações destrutivas ficam fora do MVP e exigirão decisão separada.
