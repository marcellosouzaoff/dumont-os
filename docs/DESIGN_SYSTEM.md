# Design System — DUMONT v0.3

**Estado:** linguagem visual aprovada como proposta; componentes ainda não implementados.

## Princípios

1. Editorial e legível: branco, cinza e preto predominantes, tipografia consistente.
2. Somente duas barras horizontais: barra superior discreta e Dock inferior.
3. Pasta ou janela de aplicativos aberta no centro, sem sidebar fixa na home.
4. Ícones de apps Google instalados inicialmente, nenhum app Apple/Finder.
5. Base Android preservada, com retorno ao launcher Samsung disponível.

## Paleta canônica

| Token | Nome | HEX | Função |
|---|---|---|---|
| cloud | Cloud Dancer, referência Pantone 2026 | #F1EEE7 | fundo principal |
| white | branco | #FFFFFF | janelas e painéis claros |
| charcoal | cinza-chumbo | #4E5353 | bordas e superfícies |
| black | preto | #252525 | tipografia e Dock |
| mocha | Mocha Mousse, referência Pantone 2025 | #A47864 | detalhes editoriais |
| red | vermelho | #CD212A | acentos e falhas |
| greenStatus | verde | #287D53 | disponível verificado |
| yellowStatus | amarelo | #B78319 | limitação comprovada |
| unknownStatus | cinza | #4E5353 | evidência insuficiente |

Peach Fuzz 2024 foi retirado da paleta por decisão do projeto. Orientação de composição em modo claro: 60% Cloud Dancer/branco, 25% cinza-chumbo, 12% preto e até 3% de acentos. Sinais operacionais são exceção semântica.

## Tipografia editorial

- **DM Sans** — todo texto de interface, menus, botões, painéis, acessibilidade e metadados; pesos 400, 500 e 600.
- **Cormorant Garamond** — títulos especiais, assinatura e peças editoriais; não utilizar em controles de navegação nem informações críticas.
- Escala inicial: hero 32sp, H1 28sp, H2 22sp, H3 18sp, body 16sp, supporting 14sp, caption 12sp.
- Linguagem principal da interface em pt-BR.

## Bordas e formas

- Borda padrão: 1dp em cinza-chumbo #4E5353; foco: 2dp e feedback não dependente apenas de cor.
- Raio: 12dp controles, 16dp painéis/janelas, 20dp Dock e cartões grandes.
- Espaçamento: 4, 8, 12, 16, 24, 32dp. Área clicável mínima: 48dp.
- Ícones: 20–24dp em controles e 24–32dp para Dock, conforme viewport.
- Animações curtas de 120–320ms; respeitar redução de movimento.

## Componentes

| Componente | Papel | Estados |
|---|---|---|
| DumontTopBar | nome DUMONT e indicadores | disponível/desconhecido |
| DumontDock | favoritos e execução de apps | normal/focado/indisponível |
| ApplicationFolder | painel central de aplicativos | carregando/vazio/erro/itens |
| AppTile | ícone e nome do app real | instalado/não visível/indisponível |
| SearchField | filtrar catálogo local | vazio/digitando/sem resultado |
| StatusSignal | verde/amarelo/vermelho/cinza | texto + símbolo + fonte |
| SettingsPanel | preferências do launcher | tema, Dock, restauração |
| QuickWidget (P1) | horário e multimídia | informação real/demonstração |
| LinusSecurityCard (P1) | resumo informativo | PASS/FAIL/UNKNOWN e fonte |

## Regras de ícones e marcas

Usar PackageManager para carregar o ícone e label oficiais de cada aplicativo Google realmente instalado, seguindo as restrições de visibilidade Android. Se ausente, não exibir ícone clicável falso. Nunca copiar Finder ou elementos gráficos proprietários Apple. Não embutir arquivos de marcas de terceiros sem verificar direitos.

## Adaptação aos dois dispositivos

- **Galaxy A55 / retrato:** topbar perto da câmera com respeito aos insets, janela central rolável, Dock compacto com overflow e atalhos prioritários.
- **Tab S9 FE / paisagem:** topbar estreita, janela de aplicativos centralizada em escala confortável, Dock inferior mais amplo e suporte a teclado/mouse.
- Testar recursos acessíveis e navegação por gestos. Interface não controla todo SystemUI nem Knox.

## Acessibilidade e status

Verde apenas se funcionamento confirmado; amarelo para indisponibilidade parcial; vermelho para falha; cinza para UNKNOWN. Usar ícone e descrição, não somente cor. Garantir alvos de toque >=48dp, rótulos semânticos, TalkBack, escala de fonte e contraste.

## Tokens

`design/tokens.json` é a referência canônica v0.3. `design/tokens.css` é espelho para a documentação visual. A futura aplicação Compose converterá estes tokens em tema Android.
