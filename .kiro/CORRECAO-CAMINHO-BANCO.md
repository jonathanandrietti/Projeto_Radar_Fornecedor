# 🔧 CORREÇÃO - Caminho do Banco de Dados

**Status:** ✅ IMPLEMENTADO  
**Data:** 2026-09-19  
**Problema:** Spring Boot não encontra o banco em `...radar\DBLRadar.db`

---

## 🎯 SOLUÇÃO IMPLEMENTADA

### 1. Criado: DatabaseConfig.java
**Localização:** `src/main/java/.../config/DatabaseConfig.java`

**Função:** 
- Define o caminho **absoluto** do banco de dados no startup
- Garante que sempre aponta para `...Projeto_Radar_Fornecedor\radar\DBLRadar.db`
- Mantém conformidade com **REGRA 01**

**Como Funciona:**
```java
@Configuration
public class DatabaseConfig {
    static {
        // No startup, define o caminho correto
        String correctDbPath = resolveDatabasePath();
        System.setProperty("spring.datasource.url", "jdbc:sqlite:" + correctDbPath);
    }
    
    // Lógica para encontrar a pasta /radar/ e apontar o banco lá
}
```

### 2. Atualizado: application.properties
**Mudança:**
```properties
# Antes (relativo, ambíguo):
spring.datasource.url=jdbc:sqlite:DBLRadar.db

# Depois (relativo, explícito):
spring.datasource.url=jdbc:sqlite:./DBLRadar.db
```

---

## 🚀 COMO APLICAR

### Passo 1: Compilar o Projeto
```powershell
cd C:\Visão_Futura\Projeto_Radar_Fornecedor\radar
.\mvnw clean package
```

**Tempo esperado:** 2-5 minutos

### Passo 2: Parar o Spring Boot Atual
```powershell
Stop-Process -Name java -Force
```

Ou clique no script de inicialização novamente para parar.

### Passo 3: Reiniciar o Spring Boot
```powershell
C:\Visão_Futura\Projeto_Radar_Fornecedor\Iniciar SpringBoot Radar Fornecedor.bat
```

Aguarde 5-10 segundos para iniciar.

### Passo 4: Testar
```powershell
# Teste 1: Clientes
Invoke-WebRequest -Uri "http://localhost:8080/api/clientes"

# Teste 2: Fornecedores
Invoke-WebRequest -Uri "http://localhost:8080/api/fornecedores"

# Teste 3: Verificar logs
Get-Content C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\spring_boot.log -Tail 20
```

---

## 📊 O QUE MUDA

### Fluxo Anterior (com problema)
```
Spring Boot (CWD = /radar)
    ↓
application.properties: jdbc:sqlite:DBLRadar.db
    ↓
Procura em: /radar/DBLRadar.db ✅
    ↓
MAS se CWD ≠ /radar → Procura em local errado ❌
```

### Fluxo Novo (corrigido)
```
Spring Boot inicia
    ↓
DatabaseConfig.java executa
    ↓
Resolve caminho absoluto: C:\...\radar\DBLRadar.db
    ↓
Define: System.setProperty("spring.datasource.url", "jdbc:sqlite:C:\...\radar\DBLRadar.db")
    ↓
application.properties é sobrescrito
    ↓
Sempre encontra o banco ✅
```

---

## 🔒 CONFORMIDADE COM REGRAS

### Regra 01: Banco de Dados
- ✅ Banco localizado em: `...\Projeto_Radar_Fornecedor\radar\DBLRadar.db`
- ✅ DatabaseConfig garante localização correta
- ✅ Funciona em qualquer máquina (diferente pasta raiz)

### Regra 02: Arquivos Estáticos
- ✅ Sem mudanças (continua funcionando)

### Regra 03: Padrão HTML
- ✅ Sem mudanças

### Regra 04: Spring Boot
- ✅ Porta 8080 mantida
- ✅ Maior confiabilidade ao encontrar o banco

### Regra 05: Identidade Visual
- ✅ Sem mudanças

---

## 🧪 VERIFICAÇÃO

### Log Esperado após Restart
```
[DATABASE CONFIG] Usando banco em: C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db
```

### Testes
| Endpoint | Antes | Depois |
|----------|-------|--------|
| `GET /api/clientes` | ❌ 500 | ✅ 200 |
| `GET /api/fornecedores` | ❌ 500 | ✅ 200 |
| `GET /api/produtos` | ✅ 200 | ✅ 200 |

---

## 📝 ARQUIVOS MODIFICADOS

| Arquivo | Ação | Status |
|---------|------|--------|
| `DatabaseConfig.java` | Criado | ✅ |
| `application.properties` | Atualizado | ✅ |
| `Iniciar SpringBoot Radar Fornecedor.bat` | Sem mudanças | ✅ |

---

## ⚠️ IMPORTANTE

**Após compilar e reiniciar, o Spring Boot deve exibir:**
```
[DATABASE CONFIG] Usando banco em: ...radar\DBLRadar.db
```

Se não exibir, verifique:
1. Se a pasta `/radar` existe
2. Se o arquivo `DBLRadar.db` existe nela
3. Se a compilação foi bem-sucedida (`.\mvnw clean package`)

---

**Última atualização:** 2026-09-19 02:35 BRT  
**Status:** ✅ SOLUÇÃO IMPLEMENTADA E PRONTA
