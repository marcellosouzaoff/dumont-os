# Plano de implementação e implantação — DUMONT v0.3

**Status atual (17/10/2026):** o plano v0.3 abaixo é histórico.
Kotlin Pilot v1.1 foi instalado no tablet; a v1.2 foi compilada e testada sem deploy.
Flutter visual v0.1 compilou para Web/Android, 5 testes PASS e não foi instalado.
Veja `STATUS_2026-10-17.md` e `ADR-0012-FLUTTER-VISUAL-PROTOTYPE.md`.

**Histórico v0.3:** fase de documentação; etapas futuras dependiam de gates e aprovação.

## Princípio de sequenciamento

**Documentar → validar → publicar documentação e conceitos → aprovar início do código → implementar → testar → piloto → distribuir.**

Não haverá código Android, instalação de APK, root, alterações de bootloader, Knox, ROM ou dados pessoais nesta fase.

## Roadmap por marcos

| Marco | Objetivo | Entregáveis | Gate de saída |
|---|---|---|---|
| M0 — Conceito | Consolidar escopo e nome | PRD, TRD, fluxos, cores e mockups | Aprovação documental |
| M1 — Transparência pública | Criar repositório público específico | README, documentos, tokens, imagens conceituais, avisos de marca | Link GitHub confirmado; nenhum segredo |
| M2 — Fundação Android | Inicializar primeiro código **após novo aceite** | Projeto Android Kotlin/Compose, CI, tela placeholder | Build reproduzível e permissões mínimas |
| M3 — Tablet | Shell em Tab S9 FE | Top bar, workspace, pasta central, Dock | Teste físico SM-X510; reversão |
| M4 — Smartphone | Shell em Galaxy A55 | Layout retrato, Dock adaptado, insets câmera | Teste físico SM-A556E; acessibilidade |
| M5 — Integrações | Recursos dependentes de APIs | buscas, favoritos, arquivos SAF, widgets opcionais | Testes de privacidade e estados honestos |
| M6 — Beta controlada | APK assinado para testes autorizados | release notes, roteiro, checklist e rollback | sem incidentes críticos |
| M7 — Publicação ampliada | Definir distribuição e licença | política de suporte, notas, licença apropriada | decisão explícita de lançamento |

## Detalhamento por pacote de trabalho

### M0 — Documentos (fase atual)

- [x] Redigir escopo e arquitetura candidatos.
- [x] Consolidar paleta, barras e tipografia.
- [x] Separar imagens conceituais geradas de capturas de terceiros.
- [ ] Receber aceite final do conjunto PRD/TRD/FLUXOS/PLANO.
- [ ] Registrar conta GitHub e repositório de destino escolhidos pelo proprietário.

### M1 — Repositório público (próxima etapa, sem código)

1. Confirmar proprietário GitHub (há duas contas conectadas) e nome público do repo.
2. Verificar repositório homônimo e eventuais conflitos de marca.
3. Checar cada arquivo contra segredos, caminhos internos, dados pessoais, artefatos do OSAMBA e licença/uso de imagens.
4. Criar repositório **público** separado dos projetos OSAMBA.
5. Publicar README, PRD, TRD, fluxos, plano, design system, paleta e apenas mockups DUMONT aprovados.
6. Validar links das imagens do README, visibilidade pública, branch padrão e árvore completa.
7. Registrar URL real, commit e status de publicação.

### M2 — Fundação de código (bloqueada até autorização específica)

- Estrutura Gradle/Kotlin/Compose mínima.
- Build local e CI com checks de formatação, compile e testes unitários.
- Manifesto HOME com compatibilidade verificável e sem permissões abusivas.
- Tema a partir de tokens v0.3.

### M3 — Tablet

- Componente TopBar.
- Catálogo de apps por PackageManager conforme política Android.
- Workspace/painel central ajustado à janela do tablet.
- Dock responsivo e onboarding de reversão.
- Roteiro de teste físico com captura somente mediante autorização.

### M4 — A55

- Safe areas ao redor da câmera e barras de gesto.
- Dock compacto com overflow e favoritos.
- Grade central rolável e teclado/IME.
- Rotação, fontes maiores e acessibilidade.

### M5 a M7

- Integrar apenas serviços necessários com autorização pontual.
- Separar testes funcionais de testes estéticos.
- Distribuição beta reversível; nenhuma atualização silenciosa.
- Definir licença de código e política de publicação da marca.

## Governança por papéis/agentes

| Papel | Responsabilidade | Autoridade |
|---|---|---|
| Orquestrador | coordenar sequência, decisões e evidências | não aprova riscos irreversíveis pelo usuário |
| Produto/UX | PRD, protótipos, acessibilidade e validação | recomenda mudanças; registra aceite |
| Android | TRD, implementação somente após gate | sem privilégios no firmware |
| Segurança | revisar permissões, dados e claims Knox | pode bloquear release insegura |
| QA | evidência PASS/FAIL/UNKNOWN nos dois modelos | não inventa testes |
| Documentação | README, ADR, change log e ativos | não inclui dados privados |

## Riscos, mitigação e contingência

| Risco | Mitigação | Resposta de rollback |
|---|---|---|
| Launcher interrompe navegação | teste de HOME, atividade e crash | retornar ao launcher padrão pelo Android |
| Performance ruim | benchmark real por modelo, redução de efeitos | desligar animações, voltar launcher anterior |
| App essencial não abre | consulta correta, error states, fallback | abrir pelo sistema padrão |
| Incompatibilidade com DeX | testar cenário separadamente | desativar modo específico |
| Alteração de aparência do Android nativo incompleta | comunicar limitação do launcher | manter só personalização de home |
| Marca/imagens incorretas | revisão de propriedade intelectual | remover asset do repositório com procedimento auditável |
| Vazamento via GitHub público | segredos proibidos; inspeção prévia | parar publicação e fazer resposta a incidente, inclusive rotação de segredos |

## Definição de pronto (DoD) por release

- Critérios de aceite associados a testes documentados.
- Evidências anexadas com origem, dispositivo, data e estado.
- Nenhuma regressão crítica de navegação ou privacidade.
- Reversão testada por pessoa real.
- README e changelog atualizados antes do release.

## Estado atual e dependências

**PASS:** pacote documental gerado localmente; cores e conceito definidos pela conversa.
**UNKNOWN:** aprovação documental final, conta GitHub de destino, publicação pública, execução de APK, compatibilidade HOME real.
**FAIL / Bloqueado:** instalar ROM como parte do MVP (deliberadamente fora de escopo).