# Governança e critérios de evidência — DUMONT v0.3

## Estados

- **PASS:** requisito comprovado por evidência reproduzível.
- **FAIL:** requisito testado e não atendido.
- **UNKNOWN:** sem validação suficiente; nunca interpretar como aprovado.

## Etapas e gates

M0 conceito e documentos → M1 publicação e conferência pública → M2 criação de código após aprovação → M3 teste no tablet → M4 teste no A55 → M5 integrações e QA → M6 beta reversível.

Cada mudança funcional exige rastreabilidade para PRD/TRD, alteração revisável, testes e plano de retorno. Sem commits que incluam segredos, dados pessoais, backups, logs ou artefatos de OSAMBA.

## Proteções

Android original e Knox preservados; nenhum acesso root ou ROM no MVP. Abrir aplicativos por intents oficiais; arquivos via Storage Access Framework. Os sinais verde/amarelo/vermelho devem refletir disponibilidade real e nunca declarar segurança comprovada por dados não acessíveis.

## Identidade

Nome de interface: **DUMONT**. Código público sob Apache 2.0, com identidade, mockups e marcas reservados conforme documentação de direitos. O nome poderá requerer verificação de marca antes de distribuição comercial.
