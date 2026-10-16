# M2 — Piloto experimental no Galaxy Tab S9 FE

**Data:** 16/10/2026 · **Dispositivo:** Samsung Galaxy Tab S9 FE (SM-X510) · **Estado:** PILOTO_PARCIAL_PASS

## Escopo e evidência de build

- Repositório: `marcellosouzaoff/dumont-os`, branch `feat/m2-android-foundation`.
- Código compilado: commit `edc7b1cc8ef9d743e90b7c952311b7149dfa73a7`.
- GitHub Actions: execução [38844392175](https://github.com/marcellosouzaoff/dumont-os/actions/runs/38844392175), conclusão **success** para `assembleDebug`, `testDebugUnitTest` e publicação do artefato.
- APK de depuração, SHA-256: `81a144b3f7ebab061c5d1fa7c2864523297e0bb5a2d7cdad23e8026cc6eb688d`.
- Instalado por ADB após autorização explícita do proprietário. Nenhuma operação de flash, root, unlock, alteração de Knox ou seleção automática de launcher padrão foi executada.

## Testes executados remotamente

| ID | Observação verificável | Resultado |
|---|---|---|
| TAB-01 | Tablet `SM-X510` identificado pelo ADB | PASS |
| TAB-02 | `adb install -r -t` retornou `Success` e PackageManager lista `br.com.dumont.launcher` | PASS |
| TAB-03 | `am start -W -n br.com.dumont.launcher/.MainActivity` retornou `Status: ok`; processo observado | PASS |
| TAB-04 | Captura do aplicativo real mostrou barra DUMONT, painel central, aplicativos Google e Dock inferior | PASS |
| TAB-05 | Filtro inicial Google mostrou 18 atividades; opção Todos mostrou 62; retorno ao filtro inicial | PASS |
| TAB-06 | Busca local “Maps” mostrou 1 atividade; “YouTube” mostrou 2; ao limpar a busca, retornou a 18 | PASS |
| TAB-07 | Toque no Chrome do Dock trouxe a atividade Chrome para a frente; DUMONT foi reaberto | PASS |
| TAB-08 | `cmd role get-role-holders android.app.role.HOME` continuou retornando `com.sec.android.app.launcher` antes e após o teste | PASS |
| TAB-09 | Botão HOME do Android preservou o launcher Samsung; APK DUMONT continuou instalado e reabriu | PASS |
| TAB-10 | Nenhum erro fatal de DUMONT encontrado na amostra consultada do logcat | PASS (amostragem, não garantia de ausência futura) |

## Condições específicas

- O tablet estava operando com janelas livres/ambiente de desktop; o DUMONT abriu numa janela de aplicativo. Este teste **não valida** a experiência de tela cheia como launcher padrão.
- Um `BACK` durante o teste retornou à atividade anterior; foi possível abrir o DUMONT novamente. Não foi evidenciada falha fatal nesse evento.
- A captura contém contexto de outros aplicativos e, por privacidade, foi mantida apenas no ambiente local de testes do tablet; **não publicada** no repositório público.
- Os números 18 e 62 representam atividades de lançamento visíveis, não necessariamente 18 e 62 pacotes únicos.

## Pendências / UNKNOWN

- Nenhum teste físico executado no **Galaxy A55 (SM-A556E)**.
- Definir DUMONT como HOME padrão e depois retornar à Samsung: **não testado**; preservar a escolha atual até etapa aprovada.
- Gestos, rotação, orientação fixa, teclado físico, acessibilidade/TalkBack, mudança de escala de fonte, consumo de bateria e estabilidade prolongada: **UNKNOWN**.
- Adição/remoção e persistência dos favoritos do Dock: **UNKNOWN** no dispositivo físico.
- App icon exibido na moldura da janela ainda é genérico do Android; precisa de identidade própria DUMONT.
- Fontes DM Sans/Cormorant Garamond ainda não estão incorporadas ao APK.
- A captura sugere que a grade central exige rolagem em dimensões menores de janela. Ajustar ergonomia após ensaio em tela cheia e com tamanho de fonte aumentado.

## Próximos gates

1. Ajustes visuais e acessibilidade no modo de janela livre.
2. Teste controlado de favoritos com restauração do estado.
3. Validar instalação e inicialização no A55 mediante autorização específica.
4. Somente após aprovação separada, ensaiar DUMONT como HOME padrão e demonstrar reversão para One UI.

**Conclusão:** instalação, abertura, busca, Dock e preservação da HOME Samsung foram observados em aparelho real. Não declarar conclusão do MVP, validação dos dois aparelhos, disponibilidade para distribuição pública ou aprovação da substituição da HOME.
