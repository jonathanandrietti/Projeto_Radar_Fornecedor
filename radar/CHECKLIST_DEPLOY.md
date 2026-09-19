# Checklist de Deploy - Radar Fornecedor com APIs

## ✅ Pré-Requisitos

### Hardware & Ambiente
- [ ] Servidor com 2GB+ RAM disponível
- [ ] Java 17+ instalado (`java -version`)
- [ ] Maven 3.8+ instalado (`mvn -version`)
- [ ] SQLite3 disponível (incluído no JDBC)
- [ ] Conexão com Internet (APIs públicas)
- [ ] Firewall permite porta 8080 (ou configure reverse proxy)

### Configurações do Sistema
- [ ] Pasta `radar/` com permissões de leitura/escrita
- [ ] Arquivo `DBLRadar.db` existe em `radar/` (Regra 01)
- [ ] Pasta `radar/target/` será criada durante build
- [ ] Arquivo `application.properties` configurado

## ✅ Build & Compilação

### Preparação
- [ ] Clone/Pull do repositório Git completo
- [ ] Certifique-se que você está na branch correta
- [ ] Limpar cache Maven anterior: `mvn clean`

### Compilação Java
```bash
# Executar no diretório radar/
cd C:\Visão_Futura\Projeto_Radar_Fornecedor\radar
mvnw.cmd clean compile -DskipTests
```
- [ ] Compilação sem erros (BUILD SUCCESS)
- [ ] Todos os novos Services aparecem em `target/classes/`
- [ ] Controllers registrados sem conflitos

### Build Final
```bash
mvnw.cmd clean package -DskipTests
```
- [ ] JAR criado em `target/radar-0.0.1-SNAPSHOT.jar`
- [ ] Tamanho > 50MB (inclui dependências)

## ✅ Validação de Arquivos

### Arquivos Java Criados
- [ ] `model/Categoria.java` → 50 linhas
- [ ] `model/Atividade.java` → 50 linhas
- [ ] `repository/CategoriaRepository.java` → 12 linhas
- [ ] `repository/AtividadeRepository.java` → 15 linhas
- [ ] `service/ConsultaCnpjService.java` → 160 linhas
- [ ] `service/GeolocalizacaoCepService.java` → 140 linhas
- [ ] `service/PreenchimentoAutomaticoService.java` → 110 linhas
- [ ] `service/CategoriaService.java` → 60 linhas
- [ ] `service/AtividadeService.java` → 60 linhas
- [ ] `controller/ConsultaCnpjController.java` → 80 linhas
- [ ] `controller/GeolocalizacaoCepController.java` → 110 linhas
- [ ] `controller/PreenchimentoAutomaticoController.java` → 80 linhas
- [ ] `controller/CategoriaController.java` → 70 linhas
- [ ] `controller/AtividadeController.java` → 70 linhas
- [ ] `dto/ConsultaCnpjDto.java` → 30 linhas
- [ ] `dto/ConsultaCepDto.java` → 20 linhas
- [ ] `dto/PreenchimentoAutomaticoDto.java` → 20 linhas

### Arquivos JavaScript/Frontend
- [ ] `static/js/preenchimento-automatico.js` → 600 linhas
- [ ] `static/js/testes-integracao.js` → 400 linhas
- [ ] `static/pages/fornecedores.html` → Campos categoria/atividade
- [ ] `static/pages/compradores.html` → Campos categoria/atividade
- [ ] `static/pages/representantes.html` → Campos categoria/atividade

### Documentação
- [ ] `GUIA_INTEGRACAO_API_CONSULTA.md` criado
- [ ] `RESUMO_IMPLEMENTACAO.md` criado
- [ ] `QUICK_START.md` criado
- [ ] `ARQUITETURA.md` criado
- [ ] `CHECKLIST_DEPLOY.md` (este arquivo) criado

## ✅ Banco de Dados

### Inicialização
- [ ] `DBLRadar.db` localizado em: `radar/DBLRadar.db` (Regra 01)
- [ ] Arquivo tem permissões de escrita
- [ ] Tamanho > 100KB (dados existentes)

### Tabelas Criadas (Hibernate)
Ao iniciar servidor pela primeira vez:
- [ ] Tabela `Categorias` criada (Hibernate DDL)
- [ ] Tabela `Atividades` criada (Hibernate DDL)
- [ ] Colunas `categoria_id` em `Fornecedores` (Hibernate DDL)
- [ ] Colunas `atividade_id` em `Fornecedores` (Hibernate DDL)
- [ ] Idem para `Compradores` e `Representantes`
- [ ] Foreign Keys estabelecidas
- [ ] Índices criados

**Verificar no SQLite**:
```bash
# No terminal SQLite
sqlite3 "C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db"
sqlite> .tables
# Deve listar: Categorias, Atividades, Fornecedores, Compradores, Representantes, ...

sqlite> pragma table_info(Fornecedores);
# Deve incluir: categoria_id, atividade_id
```

## ✅ Startup do Servidor

### Executar Spring Boot
```bash
cd C:\Visão_Futura\Projeto_Radar_Fornecedor\radar
mvnw.cmd spring-boot:run
```

### Verificar Inicialização
- [ ] Vê a mensagem: `Started RadarApplication in X seconds`
- [ ] Nenhuma exceção Java
- [ ] Port 8080 disponível (não ocupada)
- [ ] Logs aparecem normalmente

### Validar Conectividade
```bash
# Em outro terminal
curl http://localhost:8080/pages/fornecedores.html
# Deve retornar HTML da página
```

## ✅ Testes Básicos

### Teste 1: Consulta CNPJ (API Pública)
```bash
curl "http://localhost:8080/api/consulta-cnpj/11222333000181"
```
- [ ] Retorna HTTP 200
- [ ] JSON com campos: nome, cnpj, cidade, status
- [ ] Resposta em < 5 segundos

### Teste 2: Consulta CEP (API Pública)
```bash
curl "http://localhost:8080/api/cep/01310100"
```
- [ ] Retorna HTTP 200
- [ ] JSON com: logradouro, cidade, latitude, longitude
- [ ] Latitude ≠ 0, Longitude ≠ 0

### Teste 3: Categorias (Autenticado)
```bash
curl "http://localhost:8080/api/categorias/ativas"
```
- [ ] Retorna HTTP 200 ou 401 (depende de auth)
- [ ] JSON array (vazio ou com categorias)

### Teste 4: Atividades (Autenticado)
```bash
curl "http://localhost:8080/api/atividades/ativas"
```
- [ ] Retorna HTTP 200 ou 401
- [ ] JSON array (vazio ou com atividades)

### Teste 5: Frontend Carrega
```
http://localhost:8080/pages/fornecedores.html
```
- [ ] Página carrega sem erros
- [ ] DevTools F12 → Console: sem erros críticos
- [ ] Menu lateral carrega
- [ ] Tabela de fornecedores aparece

## ✅ Testes de Preenchimento Automático

### Teste Manual (Browser)
1. [ ] Abrir `http://localhost:8080/pages/fornecedores.html`
2. [ ] Clicar "NOVO FORNECEDOR"
3. [ ] Digitar CNPJ: `11222333000181`
4. [ ] Pressionar TAB
5. [ ] Verificar campos preenchidos:
   - [ ] Nome
   - [ ] CEP
   - [ ] Logradouro
   - [ ] Cidade
   - [ ] Estado
6. [ ] Verificar Latitude/Longitude com valores
7. [ ] Dropdown Categoria carregado
8. [ ] Dropdown Atividade carregado

### Teste via Console (DevTools)
```javascript
// F12 → Console
runAllTests()
```
- [ ] Todos 11 testes passam
- [ ] Nenhum erro em vermelho
- [ ] Relatório de sucesso exibido

## ✅ Teste de Salvamento

### Criar Registro Completo
1. [ ] Preencher formulário:
   - Nome: "Empresa Teste"
   - CNPJ: CNPJ válido
   - Categoria: Selecionar uma
   - Atividade: Selecionar uma
   - Status: Aprovado
   - Pontuação: 5.0

2. [ ] Clicar "SALVAR REGISTRO"
3. [ ] Verificar:
   - [ ] HTTP POST 201 ou 200 (DevTools Network)
   - [ ] Alerta de sucesso aparece
   - [ ] Modal fecha
   - [ ] Novo registro aparece na tabela
   - [ ] Dados coincidem com entrada

### Validar no Banco
```bash
sqlite3 "C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db"
sqlite> SELECT nome, categoria_id, atividade_id FROM Fornecedores ORDER BY id DESC LIMIT 1;
# Deve retornar: Empresa Teste | (número) | (número)
```

## ✅ Teste de Copy para /target (Regra 02)

### Copiar Arquivos Estáticos
```powershell
# Regra 02: Copiar após editar HTML/CSS/JS
Copy-Item "C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\src\main\resources\static\js\*" `
  -Destination "C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\target\classes\static\js\" -Force

Copy-Item "C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\src\main\resources\static\pages\*" `
  -Destination "C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\target\classes\static\pages\" -Force
```
- [ ] Arquivos copiados sem erros
- [ ] Timestamp atualizado em `/target`
- [ ] Refresh no browser (Ctrl+Shift+R) mostra alterações

## ✅ Teste de Performance

### Medir Tempos
```javascript
// Console
console.time('consultarCnpj');
consultarCnpj('11222333000181');
console.timeEnd('consultarCnpj');
// Esperado: < 5000ms
```

- [ ] Consulta CNPJ: < 5 segundos
- [ ] Consulta CEP: < 3 segundos
- [ ] Carregamento Categorias: < 1 segundo
- [ ] Salvamento Registro: < 500ms

### Verificar Memória
```javascript
// Chrome DevTools → Memory
// Tirar heap snapshot antes e depois
```
- [ ] Sem memory leaks
- [ ] Heap stável após 5 min de uso
- [ ] < 200MB de uso

## ✅ Teste de Erros & Edge Cases

### Testes de Erro
- [ ] CNPJ inválido (< 14 dígitos) → Erro 400
- [ ] CEP inválido (< 8 dígitos) → Erro 400
- [ ] CNPJ não existente → Erro 404 com mensagem
- [ ] Categoria não existe → Erro ao salvar
- [ ] API externa offline → Fallback ou erro informativo

### Edge Cases
- [ ] CNPJ com formatação: `11.222.333/0001-81` → Aceita
- [ ] CEP com hífen: `01310-100` → Aceita
- [ ] Caracteres especiais em Nome → Escapa corretamente
- [ ] Atualizar registro existente → Categorias/Atividades mantidas

## ✅ Segurança

### Validações
- [ ] CNPJ validado antes de enviar para API
- [ ] CEP validado antes de enviar para API
- [ ] SQL Injection não possível (JPA parametrizado)
- [ ] XSS prevenido (innerHTML escape)
- [ ] CSRF tokens se houver formulários críticos

### Autenticação
- [ ] Endpoints públicos acessíveis sem login
- [ ] Endpoints autenticados retornam 401 sem sessão
- [ ] Session timeout funciona (2 minutos por padrão)

## ✅ Configurações de Produção

### application.properties
```properties
spring.datasource.url=jdbc:sqlite:DBLRadar.db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false  # ← Desabilitar em produção
server.port=8080
logging.level.root=WARN
```
- [ ] DDL-auto em `update` (não `create`)
- [ ] show-sql em `false` em produção
- [ ] Logging level apropriado
- [ ] Port correto configurado

## ✅ Monitoramento

### Logs
- [ ] Arquivo `spring_boot.log` criado
- [ ] Logs rotacionados diariamente
- [ ] Nenhum WARN ou ERROR crítico

### Health Check
```bash
curl http://localhost:8080/actuator/health
# Se habilitado, deve retornar {"status":"UP"}
```

## ✅ Backup & Disaster Recovery

### Backup do Banco
- [ ] `DBLRadar.db` é armazenado em local seguro
- [ ] Backup diário programado
- [ ] Replicação para servidor secundário (se produção)

### Plano de Recuperação
- [ ] Documento com passos para restaurar
- [ ] Contato de suporte identificado
- [ ] Tempo de RTO definido (ex: < 1 hora)

## ✅ Documentação & Handover

### Documentos Entregues
- [ ] GUIA_INTEGRACAO_API_CONSULTA.md
- [ ] RESUMO_IMPLEMENTACAO.md
- [ ] QUICK_START.md
- [ ] ARQUITETURA.md
- [ ] CHECKLIST_DEPLOY.md (este)

### Treinamento
- [ ] Equipe de desenvolvimento treinada
- [ ] Suporte técnico conhece arquitetura
- [ ] Documentação compartilhada (Wiki/Confluence)

## ✅ Go-Live Checklist Final

### Dia do Deploy
```
[ ] 9:00  - Backup completo do banco
[ ] 9:15  - Build final: mvnw.cmd clean package -DskipTests
[ ] 9:30  - Parar servidor anterior
[ ] 9:35  - Deploy de novo JAR
[ ] 9:45  - Start Spring Boot: mvnw.cmd spring-boot:run
[ ] 10:00 - Executar testes: runAllTests()
[ ] 10:15 - Testes manuais de casos críticos
[ ] 10:30 - Liberar acesso para equipe
[ ] 11:00 - Monitoramento contínuo
[ ] 12:00 - Validação com usuários finais
```

### Critérios de Sucesso
- [ ] 100% dos testes passam
- [ ] Tempo de resposta < 5 segundos
- [ ] Nenhum erro crítico em logs
- [ ] Usuários conseguem fazer suas tarefas
- [ ] Performance aceitável (< 1s para operações básicas)

## 📞 Suporte Pós-Deploy

### Contatos
- **Tech Lead**: [Nome] - [Email] - [Telefone]
- **DBA**: [Nome] - [Email] - [Telefone]
- **Escalação**: Gerente de Infraestrutura

### Hotline 24/7
- [ ] Número de emergência documentado
- [ ] On-call engineer designado
- [ ] Runbook de troubleshooting preparado

## ✅ Sign-Off

- [ ] Desenvolvedor (Backend): _________________ Data: _____
- [ ] QA/Tester: _________________ Data: _____
- [ ] DevOps/Infra: _________________ Data: _____
- [ ] Gerente de Projeto: _________________ Data: _____

---

**Versão**: 1.0  
**Última Atualização**: Setembro 2026  
**Status**: Pronto para Deploy ✅

**Notas Adicionais**:
```
[Espaço para notas do time de deploy]
```
