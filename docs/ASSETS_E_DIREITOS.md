# Política de imagens, fontes, identidade e direitos — DUMONT

## Mockups aprovados

- `assets/mockups/dumont-tablet-e-a55-conceito.png`: imagem ilustrativa gerada na conversa; não é screenshot real.
- `assets/mockups/dumont-desktop-conceito.png`: representação conceitual; não é software em execução.
- `design/paleta.png` e `design/paleta.svg`: composição gráfica da paleta.

O uso em README deve identificar claramente os mockups como conceitos. Não publicar capturas do macOS/Apple usadas apenas como referência, dumps dos dispositivos, dados pessoais, credenciais, projetos privados OSAMBA ou logotipos isolados de terceiros.

## Direitos e licenciamento

O código futuro e a documentação original do repositório são publicados sob [Apache License 2.0](../LICENSE), salvo declaração específica. **Mockups, imagens, fotografia, logos e outros materiais gráficos não são automaticamente licenciados pela Apache 2.0** e exigem permissão/licença específica para reutilização.

A identidade DUMONT não é concedida pela licença de código. Samsung, Galaxy, Android, Google, Pantone e seus ícones e marcas pertencem a seus titulares. A referência Pantone é cromática; o projeto usa equivalentes digitais aproximados.

DM Sans e Cormorant Garamond estão sob SIL Open Font License 1.1.
As licenças verificadas ficam em `experimental/flutter_dumont/assets/fonts/`.
Os arquivos de fonte **não são commitados**; o script `setup_fonts.sh`
os obtém das fontes oficiais. Material Icons provém do Flutter SDK instalado,
sem incorporar os arquivos de fonte no repositório.

## Capturas de código vs. conceitos

As quatro imagens `experimental/flutter_dumont/test/goldens/*.png` são
**capturas do motor de renderização Flutter**, geradas automaticamente por
widget tests com ícones e dados **demonstrativos**. Não são IA generativa,
fotos do Samsung DeX, nem capturas de um APK instalado nos dispositivos.
Podem ser exibidas como provas de layout e testes de regressão visual.

## Aviso obrigatório

> Interface Flutter DUMONT em protótipo visual, **Flutter ainda não instalado**
em Galaxy A55 nem Tab S9 FE. Uma versão anterior do Pilot Kotlin foi instalada
historicamente no Tab S9 FE. Projeto independente de Samsung, Google, Apple e Pantone; marcas de terceiros pertencem aos respectivos titulares.
