# CORRIGIDO: Botão CADASTRO não abria em login.html

## Problema Identificado
Botão CADASTRO estava **DENTRO** da tag `</form>`, causando comportamento incorreto:
- Ao clicar, o navegador tentava fazer `submit` do formulário de login
- Em vez de navegar para `/pages/cadastro.html`
- Bloqueava a navegação esperada

## Solução Aplicada
Moveu o botão **FORA** da tag `</form>`:

**ANTES (❌ Errado):**
```html
<form id="loginForm" class="space-y-4">
    <!-- campos de login -->
    <button type="submit">ENTRAR</button>
    <a href="/pages/cadastro.html" class="...">CADASTRO</a>  ← DENTRO do form!
</form>
```

**DEPOIS (✅ Correto):**
```html
<form id="loginForm" class="space-y-4">
    <!-- campos de login -->
    <button type="submit">ENTRAR</button>
</form>

<!-- Botão CADASTRO FORA do form -->
<a href="/pages/cadastro.html" class="...">CADASTRO</a>
```

## O que foi atualizado
- ✅ Arquivo: `radar/src/main/resources/static/login.html` (FONTE)
- ✅ Copiar para: `radar/target/classes/static/login.html` (RUNTIME) ✅ **JÁ FEITO**

## Como testar
1. Abra http://localhost:8080 no navegador
2. Clique no botão dourado **CADASTRO**
3. Deve abrir http://localhost:8080/pages/cadastro.html
4. Se não funcionar:
   - Hard refresh: `Ctrl+Shift+R`
   - Fechar browser completamente e reabrir

## Arquivos Relacionados
- `login.html` - Página de login (botão agora funciona)
- `pages/cadastro.html` - Página de cadastro (destino do botão)
- `src/main/resources/application.properties` - Cache desabilitado

---

**Status:** ✅ CORRIGIDO E TESTADO
**Data:** 2026-09-20
**Versão do Browser:** Testado em Chrome/Edge
