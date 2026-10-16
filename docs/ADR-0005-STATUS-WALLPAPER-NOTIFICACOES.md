# ADR-0005 — Sistema no centro, wallpaper selecionável e notificações à direita

**Data:** 16/10/2026 · **Versão proposta:** DUMONT Pilot 0.7.0-dev · **Estado:** implementação sujeita a compilação e testes físicos.

## Requisitos

- No centro da barra superior: data, hora, porcentagem real da bateria e tipo de conexão ativo, lidos das APIs públicas Android. Em aparelho estreito, texto compacto; não inventar indicadores.
- Wallpaper cobre toda a tela atrás das barras. Em DUMONT → Aparência → Escolher papel de parede, abrir seletor do Android para fotos/galeria/arquivos; persistir somente URI concedida pelo usuário. Sem varrer, copiar, enviar ou baixar fotos.
- Usar papel de parede original devolve o desenho embutido e não apaga imagens pessoais.
- Notificações têm botão de sino minimalista, sem a marca DUMONT, no lado direito da barra superior. Abrem um painel lateral interno alinhado à direita, com botão Fechar.
- Notificações reais só são acessadas com consentimento explícito pelo painel oficial de Acesso às notificações Android. Sem autorização, mostrar apenas explicação/atalho para configurações.
- Listener Android apresenta títulos e textos de notificações como dados transitórios em memória. Não salva em disco, banco, logs, rede, relatórios ou telemetria. Exclui notificações marcadas VISIBILITY_SECRET.
- Hora, bateria e rede são consultadas apenas enquanto a Activity está RESUMED, em intervalos de 30 segundos. Mostra Wi-Fi, dados móveis, VPN, Ethernet ou conexão indisponível conforme APIs autorizadas.
- As duas barras continuam com exatamente a mesma largura, preto sólido #252525 e transparência ajustável. Termux permanece no menu Terminal, não no Dock.
- HOME padrão Samsung preservada. Wallpaper muda apenas dentro do DUMONT, não na One UI.

## Validação

| Gate | Critério | Estado inicial |
|---|---|---|
| UI-01 | Hora/data/bateria/conexão reais no centro | UNKNOWN |
| UI-02 | Imagem da galeria selecionada e salva com SAF | UNKNOWN |
| UI-03 | Retorno ao papel de parede original | UNKNOWN |
| UI-04 | Sino abre/fecha painel direito | UNKNOWN |
| PRIV-01 | Notificações exigem consentimento oficial Android | Pendente teste |
| PRIV-02 | Dados de notificações não persistidos nem enviados | Revisão de código |
| SAFE-01 | HOME Samsung e apps preservados | UNKNOWN |
| PERF-01 | Nenhum polling quando Activity está pausada | Revisão de código |
| BUILD | Kotlin/Gradle/CI/APK stable signing | Pendente |

A linha branca observada na janela do tablet é do Samsung DeX, não da superfície do aplicativo DUMONT.
