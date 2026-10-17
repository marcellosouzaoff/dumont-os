# Relatório de sessão — DUMONT v0.3

**Data de referência:** 15/10/2026
**Estado:** documentação pré-código.

## Decisões

- Nome de produto: DUMONT (repositório dumont-os).
- Dispositivos-alvo: Galaxy A55 SM-A556E e Tab S9 FE SM-X510.
- Android 16 preservado. Knox permanece intacto; interface de segurança chamada Linus Security.
- Barras superior e Dock inferior; painel/pasta de apps central; sem sidebar lateral fixa.
- Ícones de apps Google instalados; sem Finder nem ícones Samsung próprios.
- Paleta Cloud Dancer #F1EEE7, branco #FFFFFF, cinza-chumbo #4E5353, preto #252525, Mocha Mousse #A47864 e vermelho #CD212A.
- Tipografias DM Sans (interface) e Cormorant Garamond (editorial).
- Licenciamento de código/documentação original Apache 2.0; marcas e imagens fora do escopo.

## Publicação

Repositório público de destino: https://github.com/marcellosouzaoff/dumont-os

## Próximas validações

Conferir árvore pública, licenças de imagens e README; fechar gate pré-código; iniciar estrutura Android somente após autorização. Não foram executadas mudanças de ROM, firmware ou Knox.


## 17/10/2026 — Avanço Fase 3 Flutter

- Fonte: branch `feature/flutter-visual-prototype-20261017` no GitHub.
- Trabalho: preservada correção visual do tablet, tema Paleta 01 centralizado,
  badge PASS/WARNING/ERROR/UNKNOWN e janelas internas Flutter com foco,
  movimento, redimensionamento e fechamento. Exemplo: Linus Security
  com Knox UNKNOWN.
- Testes: Flutter 3.47.7, `flutter analyze` PASS 0 problemas e `flutter test`
  PASS 11/11 no Codespace. Dois golden regenerados pela alteração de layout.
- Instalação: pacote preview v1.0.0 confirmado por ADB no SM-X510.
  Código Fase 3 não instalado; build Android ARM64 debug PASS após
  preparação de Android SDK API 36 e NDK 28.2 no Codespace.
  APK de aproximadamente 75 MB, SHA-256: `ce7a1835cf43c860b4a166df936fdbbc20e285d3c13822d3bd2b7ce3c878dac8`.
- Publicação GitHub: commit de implementação `8131e72`; atualização
  de goldens e documentação em commit subsequente.
- Restrição: GitHub bloqueou a publicação de workflow por falta de escopo.
  O CI Flutter permanece PENDENTE.
- Próximas etapas: preparar Android SDK, compilar preview assinado em
  pacote isolado, validar janela responsiva no tablet, depois AndroidBridge.
- Sem troca de HOME, permissões sensíveis, root, flash, merge de produção
  ou impacto em OSAMBA.
