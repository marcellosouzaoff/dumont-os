# ADR-0004 — Barras com a mesma largura e Dock essencial

**Data:** 16/10/2026 · **Versão:** DUMONT Pilot 0.6.1-dev · **Estado:** implementação submetida a testes CI.

## Requisitos aprovados pelo proprietário

- Barra superior e inferior com **comprimento rigorosamente igual**, usando a largura original da barra superior como referência, adaptando-se ao tamanho efetivo da atividade Android.
- Ambas as barras compartilham margens horizontais de 12 dp no tablet e 4 dp no telefone, calculadas uma única vez pelo container principal. Não usar fator percentual de largura diferente em nenhuma das barras.
- Ambas usam a **mesma cor sólida DUMONT #252525**, com alpha independente do wallpaper, mas controlado por **um único ajuste de transparência**. Default 0% transparente; intervalo ajustável de 0% até 80%. O usuário escolhe o nível no menu aberto ao tocar DUMONT à direita.
- Dock inferior **somente com ícones de aplicativos**, sem botão Apps ou atalho padrão para Termux.
- O **Termux não aparece no Dock**, ainda que tenha histórico alto de uso ou conste em favoritos antigos. O acesso continua por Terminal na barra superior.
- Aplicativos essenciais disponíveis preenchem os espaços enquanto não houver uso real; favoritos do usuário têm prioridade, depois apps efetivamente mais usados, depois os essenciais. A lista é limitada ao que estiver instalado e possuir atividade iniciável visível ao Android.
- O wallpaper continua ocupando a tela toda atrás das duas barras, e a HOME Samsung fica como padrão até aprovação específica.

## Barra branca observada no tablet

Uma captura local do Galaxy Tab S9 FE demonstrou o DUMONT rodando dentro de uma janela Samsung DeX, com barra de título branca **fora da superfície do aplicativo**. Não remover nem simular controles do sistema dentro do DUMONT. A apresentação em tela cheia/maximizada deve ser analisada separadamente, usando funcionalidades normais do sistema.

## Validação

| Critério | Verificação |
|---|---|
| Barras de mesma largura | Ambas recebem o mesmo `barSideInset` e `fillMaxWidth()` do container |
| Cor e transparência | Cor única `DumontColors.black` e alpha ajustável de 0.20 a 1.00 |
| Primeiro Dock | Essentials instalados preenchem vagas sem obrigar o usuário a abrir apps para aparecerem |
| Exclusão de Termux | Keys do pacote `com.termux` bloqueadas por `excluded` no ranking e no evento de fixação |
| Ranking | Pins voluntários, uso real, essenciais; preserva ordem e limite |
| Segurança | Sem alteração do launcher padrão, firmware, Knox, dados dos aplicativos ou repositórios |

**Evidência esperada:** unit tests, CI PASS, APK Pilot assinado com a keystore estável. Teste visual em tablet/A55 e consumo de bateria ainda dependem de execução real.
