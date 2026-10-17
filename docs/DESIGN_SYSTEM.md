# Design System — DUMONT Paleta 01 (2026-10-17)

**Estado:** referência visual aprovada. Prova de conceito Flutter implementada e
testada; Kotlin Pilot em desenvolvimento. Seções antigas permanecem históricas.

## Princípios

1. Editorial e legível: branco, cinza e preto predominantes, tipografia consistente.
2. Somente duas barras horizontais: barra superior discreta e Dock inferior.
3. Pasta ou janela de aplicativos aberta no centro, sem sidebar fixa na home.
4. Ícones de apps Google instalados inicialmente, nenhum app Apple/Finder.
5. Base Android preservada, com retorno ao launcher Samsung disponível.

## Paleta canônica — vigente

| Token | Nome | HEX | Função |
|---|---|---|---|
| baritone | Baritone Blue | `#272836` | Fundo, TopBar, Dock e Ghost |
| cloud | Cloud Dancer | `#F0EEE9` | Janelas e texto claro |
| accent | Curds & Whey | `#BCA483` | Detalhes contidos |
| white | Branco | `#FFFFFF` | Campos e superfícies auxiliares |
| charcoal | Grafite auxiliar | `#414352` | Textos e divisões |
| red | Vermelho semântico | `#CD212A` | Falhas reais, não decorativas |
| greenStatus | Verde | `#287D53` | Estado comprovado |
| yellowStatus | Amarelo | `#B78319` | Limitação comprovada |

Valores HEX digitais, não equivalências Pantone certificadas. Cloud
`#F1EEE7`, preto `#252525` e Mocha `#A47864` são referências legadas.

## Tipografia editorial

- **DM Sans** — todo texto de interface, menus, botões, painéis, acessibilidade e metadados; pesos 400, 500 e 600.
- **Cormorant Garamond** — títulos especiais, assinatura e peças editoriais; não utilizar em controles de navegação nem informações críticas.
- Escala inicial: hero 32sp, H1 28sp, H2 22sp, H3 18sp, body 16sp, supporting 14sp, caption 12sp.
- Linguagem principal da interface em pt-BR.

## Bordas e formas

- Borda padrão: 1dp em cinza neutro #9999A2; foco: 2dp e feedback não dependente apenas de cor.
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

`design/tokens.json`, `design/tokens.css` e `design/paleta.svg`
registram a Paleta 01 nesta branch. `experimental/flutter_dumont/lib/main.dart`
utiliza as mesmas cores. Fontes DM Sans e Cormorant Garamond vêm de fontes
oficiais sob SIL OFL e são obtidas por script, sem versionar arquivos de fonte.

O Flutter v0.1 renderiza UI real com aplicativos fictícios; integração com
PackageManager, notificações e Samsung DeX continua pendente.
