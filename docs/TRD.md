# TRD — DUMONT OS v0.3

**Tipo:** Technical Requirements Document · **Status:** arquitetura proposta, não implementada · **Data-base:** 15/10/2026

## 1. Decisão central

**Android original preservado + launcher DUMONT via APK normal.** Não criar ROM, SystemUI, root, bootloader unlock ou serviços privilegiados na etapa inicial. A substituição da tela inicial depende da seleção explícita do usuário.

## 2. Alvos e evidências

| Item | SM-A556E (A55) | SM-X510 (Tab S9 FE) |
|---|---|---|
| Android / API | 16 / 36 | 16 / 36 |
| One UI | 8.5 | 8.5 |
| Bootloader | locked | locked |
| Verified Boot | green | green |
| Knox Warranty Bit | 0 | 0 |
| SDK/ambiente da equipe | UNKNOWN | UNKNOWN |
| Compatibilidade do launcher implementado | UNKNOWN | UNKNOWN |

Valores observados remotamente por comandos de leitura em 15/10/2026. **Não** representam teste de APK ou build.

## 3. Stack proposta (a validar na implementação)

- Linguagem: **Kotlin**.
- Interface: **Jetpack Compose**, componentes próprios DUMONT, arquitetura adaptativa com WindowSizeClass.
- Padrão: app Android convencional que declara atividade de início compatível com `ACTION_MAIN` e `CATEGORY_HOME`, sujeito ao gerenciamento do sistema e preferência do usuário.
- Estado local: Preferences DataStore para escolhas visuais/favoritos; Room apenas se houver dados relacionais reais.
- Navegação: estado de home, pasta central, lista de aplicativos, favoritos e configurações próprias.
- Arquivos (P1): Storage Access Framework com autorização por pasta/documento.
- Logs: depuração local e sanitização, sem enviar arquivos ou dados privados por padrão.
- Build/CI futuros: Gradle + Android Gradle Plugin + Android SDK; versões exatas travadas em decisão de implementação, não neste documento.

## 4. Limites da camada

```text
HARDWARE GALAXY (inalterado)
       ↓
ANDROID 16 + ONE UI / KNOX (inalterados)
       ↓ APIs públicas e permissões concedidas
DUMONT LAUNCHER (aplicativo normal)
   ├── TopBar / marca DUMONT / indicadores
   ├── AppCatalog / busca / visibilidade permitida
   ├── CenterWorkspace / grade e pastas DUMONT
   ├── BottomDock / favoritos / atalhos
   ├── Settings / temas / preferências
   ├── StatusEngine / PASS-LIMITED-UNAVAILABLE-UNKNOWN
   └── Widgets e Linus Security (P1, apenas dados acessíveis)
       ↓
APLICATIVOS INSTALADOS (abertos pelo Android)
```

O launcher não é um gerenciador universal de janelas. Janelas exibidas na home são **painéis próprios**; apps de terceiros iniciam conforme as regras e modos do Android.

## 5. Módulos e contratos

| Módulo | Responsabilidade | Entradas | Saídas |
|---|---|---|---|
| `Shell` | Barra superior, workspace, Dock | configuração, dimensão da janela | UI adaptada |
| `AppCatalog` | descobrir e abrir apps permitidos | PackageManager e intents suportadas | app id, título, ícone e ação de launch |
| `DockManager` | favoritos e overflow | seleções do usuário, apps disponíveis | ordem persistida |
| `Workspace` | pasta/grade central | filtro, categorias, tamanho | painel centrado/rolável |
| `ThemeEngine` | cores, fontes, raios e estados | tokens v0.3, tema | semântica visual coerente |
| `StatusEngine` | estado confiável dos recursos | sondas autorizadas | verde/amarelo/vermelho/cinza + mensagem |
| `Preferences` | escolhas locais | eventos do usuário | armazenamento mínimo |
| `SecurityView` P1 | informar proteção acessível | propriedades públicas/usuário | diagnóstico com fonte e data; UNKNOWN quando impossível |
| `FileGateway` P1 | abrir arquivos autorizados | URI SAF com concessão | leitura/navegação sem varredura global |

## 6. Permissões — princípio do mínimo necessário

- Não solicitar root, Accessibility Service, Device Admin, leitura de SMS/contatos/localização ou armazenamento amplo na v1.
- Usar intenções e funcionalidades permitidas para apps HOME; documentar filtros e visibilidade exigidos.
- Preferir declaração de `<queries>` para casos específicos. `QUERY_ALL_PACKAGES` só poderá ser considerado após justificativa técnica e checagem de política de loja; não incluí-lo por padrão.
- Para arquivos, solicitar consentimento pelo seletor SAF, não acesso indiscriminado à memória.
- Não reproduzir telas falsas de configurações de segurança ou estado Knox.

## 7. Ícones e propriedade intelectual

- Obter ícones em execução via APIs Android dos aplicativos instalados. Não embutir assets de Google, Samsung, Apple, Finder ou logos de terceiros no repositório.
- Usar ícones genéricos próprios ou licenciados apenas onde não houver app instalado ou como representações de funcionalidades DUMONT.
- As imagens conceituais do README contêm marcas de apps como representação de conceito; mantê-las com disclaimer e atribuição.
- Não copiar a aparência exata de apps Apple; replicar princípios gerais de layout e interação com identidade própria.

## 8. Política para estados coloridos

| Estado | Cor | Uso | Regra |
|---|---|---|---|
| AVAILABLE | Verde `#287D53` | Pronto e verificadamente utilizável | prova de disponibilidade, origem e momento da verificação |
| LIMITED | Amarelo `#B78319` | Recurso parcialmente disponível | informar condição faltante |
| UNAVAILABLE | Vermelho `#CD212A` | Bloqueado/erro | informar causa quando identificável |
| UNKNOWN | Cinza `#4E5353` | Sem acesso à evidência | estado padrão ao faltar fonte confiável |

No topo ou abas, indicador é um **sinal de estado**, não equivale aos controles de fechar/minimizar/maximizar de macOS.

## 9. Contratos de layout

- Inset de status/câmera e zona inferior reservada por WindowInsets.
- Breakpoints derivados de WindowSizeClass, sem hardcode de centímetros/modelo.
- Tablet paisagem e A55 retrato são perfis testados, não únicos.
- Dock overflow quando ícones não cabem, nunca sobrepondo gestos essenciais.
- Texto escalável, mínimo 48dp para ações; usar formato de paisagem/retrato e densidades reais nos testes.
- Nenhuma funcionalidade depende só de cor.

## 10. Testes necessários (não executados)

| Suite | Caso | Gate |
|---|---|---|
| Unit | tokens, favoritos, filtros, regras de estado | 100% casos críticos aprovados |
| UI | Dock, pastas, navegação, dark/light, escala de fonte | sem bloqueios graves |
| Instrumentado | abrir e voltar de apps instalados em A55 e tablet | PASS em ambos |
| Segurança | sem root, sem credenciais em logs, permissões mínimas | PASS |
| Recuperação | remover launcher padrão e voltar à Samsung | PASS |
| Físico | bateria, fluidez, rotação, borda, câmera frontal | PASS segundo métricas registradas |

## 11. Telemetria e dados

Versão inicial: nenhuma telemetria remota obrigatória. Logs de diagnóstico devem ser locais, revisáveis e nunca conter tokens, e-mails, capturas privadas, nomes de documentos pessoais ou chaves. Qualquer analytics futuro exige decisão específica e política de privacidade.

## 12. Dependências e riscos técnicos

1. HOME role disponível conforme ROM/fabricante: **UNKNOWN até validação em APK real**.
2. Exibição universal de apps pode exigir comportamento próprio de launcher sob filtros Android.
3. Terceiros não garantem ícones consistentes, suporte a freeform ou widgets.
4. Widgets e indicadores de sistema não devem ser simulados como reais.
5. DeX pode alterar comportamento e ergonomia; tratá-lo como cenário de teste, não requisito garantido.

**Leituras:** https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive ; https://developer.android.com/training/package-visibility/declaring ; https://developer.android.com/guide/topics/providers/document-provider ; https://developer.android.com/guide/components/activities/tasks-and-back-stack.html

## 13. Critérios para sair da etapa documental

- [ ] PRD e fluxos ratificados.
- [ ] Restrições públicas do Android e recursos Samsung distinguidos.
- [ ] Manifesto de permissões planejado revisado.
- [ ] Plano de testes nos dois dispositivos aceito.
- [ ] Nenhum requisito exige desbloqueio/root no MVP.
- [ ] Repositório público com documentação e assets verificados, antes do primeiro commit de aplicativo.
