# ADR-0014 — AndroidBridge Flutter ↔ Kotlin — Catálogo e inicialização

**Data:** 17/10/2026
**Escopo:** etapa inicial da integração nativa, branch experimental,
sem substituir o aplicativo HOME ou o piloto Kotlin.

## Contrato implementado

Canal: `br.com.dumont.experience/native`

| Operação | Entrada | Saída |
|---|---|---|
| `listApps` | sem parâmetros | lista de `label`, `packageName`, `component` |
| `launchApp` | `{component: String}` | `true` ou erro tipado |

- Android consulta somente atividades exportadas com intent
  `ACTION_MAIN + CATEGORY_LAUNCHER` por `PackageManager`.
- O manifesto declara apenas a consulta específica em `<queries>`.
- Não usa `QUERY_ALL_PACKAGES`, permissões privilegiadas, root,
  acessibilidade, Knox APIs privadas ou acesso às notificações.
- Na abertura, o Kotlin reconsulta a lista permitida antes de executar
  `startActivity`; não aceita componentes arbitrários.
- Na indisponibilidade do canal, a interface Flutter permanece
  demonstrativa, sem afirmar que os apps ilustrativos estão instalados.
- Quando o canal responde, a grade usa aplicativos reais, com
  ícones genéricos temporários. Ícones nativos reais estão pendentes.
- App preview: `br.com.dumont.experience.preview`, distinto do piloto.
- Janelas de terceiros seguem gerenciadas pelo Android/DeX.

## Evidências

- RED: testes de contrato falharam sem o módulo AndroidBridge.
- GREEN: `flutter analyze` sem problemas e `flutter test` 14/14,
  incluindo três testes de contrato/catálogo.
- Build Kotlin/Android ARM64: **PASS**, APK de debug compilado no Codespace
  (Flutter 3.47.7, SDK 36, NDK 28.2).
  SHA-256: `fe3d629c3991563703fc1f839b256fad19df123b5e17d00cb78d4cee4eaac47c`.
- Instalação/teste com aplicativos reais no SM-X510: UNKNOWN.
- Sem alteração da HOME Samsung, nem solicitação de novas permissões.

## Próximos gates

1. Compilar APK debug ARM64 com novo canal Kotlin.
2. Registrar SHA-256 e conferir manifesto/pacote.
3. Testar fisicamente catálogo, toque para abrir apps e retorno ao DUMONT.
4. Integrar ícones reais por canal eficiente, sem vazamento de dados.
5. Depois: estado do dispositivo, ajustes públicos e notificações
   somente com permissão explícita.
