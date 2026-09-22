# ✅ Conectar ao H2 Console — Forma Correta

**Erro Comum:** Digitar URL do navegador na JDBC URL

---

## 🚨 O Erro

```
Invalid JDBC URL: http://localhost:8080/h2-console
```

**Motivo:** Você digitou a URL do NAVEGADOR, mas H2 Console pede a URL JDBC do BANCO.

---

## ✅ A Forma Correta

### Tela de Conexão H2 Console

Quando abrir http://localhost:8080/h2-console, verá uma tela assim:

```
┌─────────────────────────────────────────────┐
│ Connect to 'localhost (2)'                  │
├─────────────────────────────────────────────┤
│                                             │
│  JDBC URL: [_______________________]       │
│  User Name: [_______________________]      │
│  Password: [_______________________]       │
│                                             │
│           [OK]  [Cancel]                   │
└─────────────────────────────────────────────┘
```

### Preencher Corretamente

| Campo | Valor |
|-------|-------|
| **JDBC URL** | `jdbc:h2:./DBLRadar;MODE=MySQL` |
| **User Name** | `sa` |
| **Password** | (deixe vazio) |

### ⚠️ NÃO Digitar

```
❌ http://localhost:8080/h2-console    ← URL do navegador (errado!)
❌ localhost:8080                       ← Servidor (errado!)
❌ 127.0.0.1:8080                      ← IP (errado!)
```

### ✅ Digitar EXATAMENTE

```
✅ jdbc:h2:./DBLRadar;MODE=MySQL       ← JDBC URL (correto!)
```

---

## 📋 Passo a Passo Visual

### 1. Abra navegador
```
http://localhost:8080/h2-console
```

### 2. Verá página branca do H2 Console com formulário

### 3. Preencha campos:

```
JDBC URL:    jdbc:h2:./DBLRadar;MODE=MySQL
             ↑
             Digite ISTO (URL JDBC, não HTTP!)

User Name:   sa

Password:    (deixe em branco)
```

### 4. Clique OK

### 5. ✅ Conecta ao banco!

---

## 🎯 Entender a Diferença

### URL do Navegador (HTTP)
```
http://localhost:8080/h2-console
```
- Acessa a interface WEB do H2 Console
- Você digita ISSO na barra do navegador

### JDBC URL do Banco (JDBC)
```
jdbc:h2:./DBLRadar;MODE=MySQL
```
- Especifica o banco de dados
- Você digita ISSO no formulário H2 Console
- Começam com `jdbc:` (não `http://`)

---

## 🧪 Teste Agora

1. Abra navegador: http://localhost:8080/h2-console
2. Copie/Cole exatamente:
   ```
   JDBC URL: jdbc:h2:./DBLRadar;MODE=MySQL
   ```
3. User Name: `sa`
4. Password: (vazio)
5. Clique **OK**

**Resultado esperado:** ✅ Conecta e mostra tabelas

---

## 📝 Componentes Explicados

### `jdbc:h2`
- Prefixo JDBC para H2 Database

### `./DBLRadar`
- Caminho relativo do arquivo do banco
- `.` = pasta atual (raiz do projeto)
- `DBLRadar` = nome do arquivo (sem .mv.db)

### `;MODE=MySQL`
- Modo de compatibilidade com MySQL
- Permite usar sintaxe MySQL

### Completo:
```
jdbc:h2:./DBLRadar;MODE=MySQL
```
= "Conectar ao banco H2 chamado 'DBLRadar' na pasta atual, em modo MySQL"

---

## ✅ Pronto!

Agora você sabe:
1. ✅ H2 Console = interface web em `http://...`
2. ✅ JDBC URL = endereço do banco em `jdbc:...`
3. ✅ Copiar/colar corretamente em cada campo

**Tente novamente com a JDBC URL correta!** 🎉
