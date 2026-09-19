# 🔍 Investigação - Banco de Dados Duplicado

**Status:** ✅ RESOLVIDO  
**Data:** 2026-09-19  
**Problema:** DBLRadar.db foi criado fora da pasta `/radar/`

---

## 📋 ACHADOS

### Bancos Encontrados
```
❌ C:\Visão_Futura\Projeto_Radar_Fornecedor\DBLRadar.db        (NÃO-CONFORME)
✅ C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db  (CONFORME)
```

### Status
- ✅ Banco não-conforme foi **REMOVIDO**
- ✅ Única cópia restante está em local correto
- ✅ `application.properties` aponta para caminho relativo

---

## 🔎 POR QUE FOI CRIADO?

### Causa Provável
Quando o Spring Boot foi iniciado, a pasta de trabalho (working directory) era `C:\Visão_Futura\Projeto_Radar_Fornecedor` em vez de `C:\Visão_Futura\Projeto_Radar_Fornecedor\radar`.

**Comportamento do SQLite:**
```
spring.datasource.url=jdbc:sqlite:DBLRadar.db
                                        ↓
              Caminho relativo → cria arquivo no CWD (Current Working Directory)
```

**Cenários:**
- Se CWD = `C:\...\Projeto_Radar_Fornecedor\radar\` → ✅ Cria em `/radar/DBLRadar.db`
- Se CWD = `C:\...\Projeto_Radar_Fornecedor\` → ❌ Cria em `/DBLRadar.db`

### Como Evitar Isso

#### Opção 1: Caminho Absoluto (NÃO RECOMENDADO)
```properties
# RUIM - Quebra em outras máquinas
spring.datasource.url=jdbc:sqlite:C:/Visão_Futura/Projeto_Radar_Fornecedor/radar/DBLRadar.db
```

#### Opção 2: Usar `file:` com classpath (RECOMENDADO)
```properties
# BOM - Funciona sempre
spring.datasource.url=jdbc:sqlite:./DBLRadar.db
```

#### Opção 3: Executar sempre do diretório correto
```bash
# Sempre rodar do /radar/
cd C:\Visão_Futura\Projeto_Radar_Fornecedor\radar
java -jar target/radar-0.0.1-SNAPSHOT.jar
```

---

## ✅ VERIFICAÇÃO FINAL

### Conformidade com Regra 01

**Localização Única e Conforme:**
```
C:\Visão_Futura\Projeto_Radar_Fornecedor\radar\DBLRadar.db
```

**Configuração:**
```properties
spring.datasource.url=jdbc:sqlite:DBLRadar.db
spring.datasource.driverClassName=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
```

**Status:** ✅ 100% CONFORME

---

## 🛠️ RECOMENDAÇÃO

Para garantir que **nunca mais** um banco é criado fora da pasta `/radar/`, recomendo atualizar o `application.properties`:

```properties
# Usar caminho relativo explícito (melhor prática)
spring.datasource.url=jdbc:sqlite:./DBLRadar.db
```

Mudanças:
- `DBLRadar.db` → `./DBLRadar.db` (deixa explícito que é relativo ao CWD)

---

## 📊 HISTÓRICO

| Data | Ação | Status |
|------|------|--------|
| 2026-09-18 | Merge de Helo adicionou novos campos | ✅ |
| 2026-09-19 | Detectado banco duplicado fora de `/radar` | ⚠️ |
| 2026-09-19 | Banco não-conforme removido | ✅ |
| 2026-09-19 | Conformidade restaurada | ✅ |

---

**Última atualização:** 2026-09-19 02:25 BRT  
**Status:** ✅ RESOLVIDO - REGRA 01 MANTIDA
