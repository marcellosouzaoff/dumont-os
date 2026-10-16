# PRD — DUMONT OS v0.3

**Tipo:** Product Requirements Document · **Fase:** pré-código · **Data-base:** 15/10/2026 · **Status:** candidato à aprovação

## 1. Problema e oportunidade

A experiência visual de fábrica dos dois aparelhos não corresponde à preferência do proprietário por uma interface desktop editorial, unificada e separada visualmente da Samsung. Queremos uma experiência que seja facilmente reconhecida como **DUMONT**, com produtividade, navegação simples, tema consistente, e sem depender de desbloqueio do bootloader.

## 2. Visão do produto

DUMONT é inicialmente um **launcher Android** com barra superior, pasta/área central e Dock inferior, responsável pela navegação principal entre aplicativos e áreas de trabalho. Pode ganhar utilitários próprios em versões futuras, sem confundir a camada de interface com o sistema operacional Android.

**Proposta de valor:** uma experiência visual e de navegação original, consistente e reversível em smartphone e tablet.

## 3. Público-alvo

- **P0 / Usuário principal:** proprietário do A55 e Tab S9 FE, utiliza Google Apps, GitHub, Termux/Ubuntu e ferramentas de trabalho e música.
- **P1 / Usuários técnicos:** testadores Android com interesse em launcher de produtividade, interface desktop e layouts adaptativos.
- **Fora do foco da v1:** troca completa de firmware, root, bypass de proteções, mercados empresariais gerenciados e aparelhos não testados.

## 4. Metas e critérios objetivos

| ID | Meta | Critério verificável |
|---|---|---|
| G1 | Reconhecimento visual | Numa avaliação cega com usuários, a maioria identifica interface diferente da One UI; coletar resultado real |
| G2 | Navegação | Abrir apps presentes pelo Dock e grade central, com retorno à home |
| G3 | Adaptabilidade | Mesmo produto em A55 retrato e S9 FE paisagem, sem cortes de texto ou toques impossíveis |
| G4 | Segurança | Operar sem root, OEM unlock, desligar Knox, desinstalar apps do sistema ou gravar firmware |
| G5 | Reversibilidade | Usuário consegue escolher novamente a tela inicial Samsung sem apagar dados |
| G6 | Confiabilidade | Indicadores verde/amarelo/vermelho derivam de estados verificáveis; estado desconhecido nunca é verde |

## 5. Escopo funcional da versão mínima (MVP)

### P0 — Obrigatório para o primeiro piloto

- **FR-001 Home:** tela DUMONT selecionável como launcher padrão, mediante escolha do usuário.
- **FR-002 Barra superior:** exibir apenas `DUMONT` à esquerda e indicadores de sistema permitidos pelo Android à direita; sem logo.
- **FR-003 Dock inferior:** mostrar ícones dos apps instalados, favoritos fixáveis/removíveis, indicação de seleção sem fingir multitarefa do sistema.
- **FR-004 Janela central:** grade de apps em pasta/painel centralizado no tablet e rolável/adaptada no A55.
- **FR-005 Busca:** filtrar a lista de apps visíveis e permitidos ao launcher; busca local inicial.
- **FR-006 Iconografia:** usar ícone publicado pelo próprio app instalado, com aparência coerente via molduras e espaçamento do DUMONT; não substituir APKs nem distribuir logotipos Apple/Samsung.
- **FR-007 Aparência:** paleta aprovada, bordas cinza-chumbo, tipografia padronizada; modo claro predominante e modo escuro funcional.
- **FR-008 Estado de recursos:** verde = verificado utilizável, amarelo = condição limitada, vermelho = indisponível, cinza = não verificado; ícone e texto juntos.
- **FR-009 Dispositivos:** teste real em SM-A556E e SM-X510, layouts responsivos.
- **FR-010 Reversão:** desmarcar DUMONT como launcher padrão e voltar à experiência anterior sem ações destrutivas.
- **FR-011 Onboarding:** comunicar permissões opcionais, limitações e como reverter o launcher.

### P1 — Após piloto aprovado

- **FR-012 Widgets próprios:** relógio, calendário e mídia usando permissões/APIs adequadas e dados reais; estados de demonstração marcados.
- **FR-013 Linus Security:** painel informativo de segurança, sem alegar substituição ou auditoria completa do Knox.
- **FR-014 Integrações:** atalhos configuráveis para Termux/Ubuntu, GitHub, ferramentas de IA e trabalho.
- **FR-015 Arquivos:** navegação por documentos e pastas selecionadas pelo usuário via Storage Access Framework (SAF).
- **FR-016 Acessibilidade:** suporte a TalkBack, escala de fonte, contraste, navegação teclado e foco.

### P2 — Pesquisa, não compromisso da versão inicial

- Experimentos com janelas internas do DUMONT, atalhos de teclado, layouts de múltiplas áreas e integração com modos desktop de Android.
- Sincronização entre dispositivos com consentimento explícito e estratégia de privacidade aprovada.

## 6. Fora do escopo e restrições explícitas

- Instalar ROM, modificar bootloader, root, remover Knox, contornar SafetyNet/Play Integrity ou apps bancários.
- Modificar SystemUI, status bar real, painel rápido de sistema ou notificações privilegiadas sem suporte oficial.
- Prometer janelas livres de todos os aplicativos de terceiros — isso depende do sistema operacional e do suporte de cada app.
- Remover aplicativos Samsung do firmware, renomear componentes proprietários, mascarar a origem do dispositivo.
- Coletar todos os arquivos, contatos, mensagens ou credenciais sem necessidade.
- Publicar arquivos de usuário, segredos, telas do macOS de terceiros ou códigos protegidos no GitHub.

## 7. Requisitos não funcionais

| ID | Dimensão | Regra/medição planejada |
|---|---|---|
| NFR-01 | Desempenho | Avaliar tempo de abertura, scroll e latência de toque nos dois modelos; orçamento numérico após baseline |
| NFR-02 | Memória/bateria | Não manter serviços permanentes desnecessários; coletar baseline comparativo com launcher padrão |
| NFR-03 | Privacidade | Processamento local por padrão, mínimo de permissões, sem telemetria na primeira versão |
| NFR-04 | Segurança | Sem permissões privilegiadas, sem APK desconhecido automaticamente instalado |
| NFR-05 | Compatibilidade | Android 16 observado nos dois aparelhos; outras versões apenas após teste |
| NFR-06 | Acessibilidade | 48dp ou mais para alvos de toque; texto escalável; estados não dependem apenas de cores |
| NFR-07 | Qualidade editorial | DM Sans interface; Cormorant Garamond em títulos decorativos; terminologia pt-BR uniforme |
| NFR-08 | Recuperação | Documentar troca do launcher padrão e desinstalação; nenhum dado do usuário removido |

## 8. Experiência e regras de layout

### Tablet SM-X510

1. Barra superior fina, horizontal e fixa.
2. Painel central de aplicativos, exibido como janela flutuante dentro da home, sem fingir janela de app externo.
3. Dock flutuante inferior com quantidade responsiva de favoritos; overflow quando necessário.
4. Sem sidebar lateral fixa na home aprovada.

### Smartphone SM-A556E

1. Barra superior próxima da câmera frontal respeitando recortes/safe areas.
2. Área central adaptada à largura e altura disponíveis, rolável se necessário.
3. Dock inferior com ícones principais e alvos de toque adequados.
4. Mesma identidade do tablet, mas sem reduzir tudo até ficar ilegível.

## 9. Dependências e riscos

- Seleção do launcher padrão depende de fluxo Android e consentimento do usuário.
- Enumeração de aplicativos depende de regras de visibilidade de pacotes do Android.
- Interface externa de apps e elementos de sistema não são alteráveis pelo launcher.
- Acesso a arquivos deve respeitar SAF e permissões efetivamente concedidas.
- Integração com Knox/indicadores pode ser limitada; exibir UNKNOWN honestamente.
- Uso público do nome e dos mockups exige conferência de marca e direitos autorais.

## 10. Critérios de aceite do PRD

- [ ] Texto de visão e limites confirmado.
- [ ] Todos requisitos P0 possuem roteiro de teste verificável.
- [ ] Screenshots conceituais correspondem à barra superior + painel central + Dock inferior.
- [ ] Compatibilidade não é confundida com instalação comprovada.
- [ ] Governança de privacidade, reversão e sem root aprovada.
- [ ] Requisitos P1/P2 mantidos fora do piloto inicial.

**Estado:** documento redigido, sujeito à aprovação final do proprietário. Nenhum código do produto criado.
