# Regras Atualizadas - Cache do Spring Boot

## ⚠️ IMPORTANTE - Tarefa de Manutenção

**A PARTIR DE AGORA**, toda vez que for **atualizar código ou executar o Spring Boot**, execute este procedimento para limpar cache:

### Procedimento de Limpeza de Cache

```powershell
# 1. Parar o servidor Java
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# 2. Deletar banco de dados H2 (cache)
cd "C:\Visão_Futura\Projeto_Radar_Fornecedor\radar"
Remove-Item -Force "DBLRadar.mv.db" -ErrorAction SilentlyContinue
Remove-Item -Force "DBLRadar.trace.db" -ErrorAction SilentlyContinue

# 3. Limpar cache Maven
Remove-Item -Recurse -Force "target" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force ".mvn/wrapper" -ErrorAction SilentlyContinue

# 4. Restaurar Maven wrapper
git restore .mvn

# 5. Rebuild
& ".\mvnw.cmd" clean package -DskipTests

# 6. Copiar arquivos estáticos para /target
Copy-Item -Recurse -Force "src\main\resources\static\*" "target\classes\static\"

# 7. Iniciar servidor
& ".\mvnw.cmd" spring-boot:run
```

---

## Por Que Isso é Necessário?

### Problema Identificado:
- Spring Boot cache de arquivos estáticos em `/target/classes/static/`
- Atualizações em `/src/main/resources/static/` **NÃO** refletem automaticamente
- H2Database mantém dados em cache até deletar `DBLRadar.mv.db`
- Maven wrapper pode corromper com limpezas incompletas

### Sintomas:
- ❌ Botões novos não aparecem na página
- ❌ Formulários antigos continuam sendo servidos
- ❌ Scripts atualizados não carregam
- ❌ Banco de dados não reseta mesmo depois de alterar schema

### Solução:
- ✅ Deletar banco H2
- ✅ Limpar /target e .mvn
- ✅ Rebuild completo
- ✅ Copiar arquivos estáticos
- ✅ Iniciar fresh

---

## Regra 07 - Limpeza de Cache (NOVA)

**Ao atualizar código ou arquivos estáticos, SEMPRE:**

1. Parar o servidor Java: `Get-Process java | Stop-Process -Force`
2. Deletar banco: `Remove-Item DBLRadar.mv.db`
3. Limpar Maven: `Remove-Item -Recurse target, .mvn/wrapper`
4. Rebuild: `mvnw clean package -DskipTests`
5. Copiar estáticos: `Copy-Item src/main/resources/static/* target/classes/static/`
6. Iniciar: `mvnw spring-boot:run`

**Nenhuma exceção é permitida sem aprovação explícita do usuário.**

---

## Checklist de Manutenção

Antes de cada execução:
- [ ] Banco deletado (`DBLRadar.mv.db` não existe)
- [ ] /target limpo
- [ ] .mvn restaurado (`git restore .mvn`)
- [ ] Build executado com sucesso
- [ ] Arquivos copiados para /target
- [ ] Servidor iniciado
- [ ] Testado endpoint: `GET /login.html`
- [ ] Botão CADASTRO presente no HTML servido

---

## Confirmação

✅ **APLICADO EM:** 22/09/2026 às 12:42  
✅ **VERIFICADO:** Botão CADASTRO agora aparece em http://localhost:8080/login.html  
✅ **PÁGINA CADASTRO:** Acessível em http://localhost:8080/pages/cadastro.html (Status 200)
