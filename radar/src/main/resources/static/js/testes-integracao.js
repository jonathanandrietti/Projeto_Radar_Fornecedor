/**
 * Script de Testes de Integração
 * Execute no console do navegador: runAllTests()
 */

const API_BASE = 'http://localhost:8080/api';

/**
 * Teste 1: Validar CNPJ Formato
 */
async function testarValidacaoCnpj() {
    console.log('\n=== TESTE 1: Validação de CNPJ ===');
    
    const cnpjValido = '11222333000181';
    const cnpjInvalido = '12345678901234';
    
    // Teste com CNPJ válido
    console.log(`Testando CNPJ válido: ${cnpjValido}`);
    let response = await fetch(`${API_BASE}/consulta-cnpj/validar/${cnpjValido}`);
    let dados = await response.json();
    console.log('Resultado:', dados);
    
    // Teste com CNPJ inválido
    console.log(`\nTestando CNPJ inválido: ${cnpjInvalido}`);
    response = await fetch(`${API_BASE}/consulta-cnpj/validar/${cnpjInvalido}`);
    dados = await response.json();
    console.log('Resultado:', dados);
}

/**
 * Teste 2: Consultar CNPJ
 */
async function testarConsultaCnpj() {
    console.log('\n=== TESTE 2: Consulta de CNPJ ===');
    
    const cnpj = '11222333000181'; // Serenata
    console.log(`Consultando CNPJ: ${cnpj}`);
    
    try {
        const response = await fetch(`${API_BASE}/consulta-cnpj/${cnpj}`);
        const dados = await response.json();
        
        if (dados.erro) {
            console.error('❌ Erro:', dados.mensagem);
        } else {
            console.log('✓ Sucesso! Dados obtidos:');
            console.table({
                'Nome': dados.nome,
                'Razão Social': dados.razaoSocial,
                'CNPJ': dados.cnpj,
                'CEP': dados.cep,
                'Cidade': dados.cidade,
                'Estado': dados.estado,
                'Atividade': dados.atividade,
                'Status': dados.status
            });
        }
    } catch (erro) {
        console.error('❌ Erro na requisição:', erro.message);
    }
}

/**
 * Teste 3: Validar CEP
 */
async function testarValidacaoCep() {
    console.log('\n=== TESTE 3: Validação de CEP ===');
    
    const cepValido = '01310100';
    const cepInvalido = '1234567';
    
    console.log(`Validando CEP válido: ${cepValido}`);
    let response = await fetch(`${API_BASE}/cep/validar/${cepValido}`);
    let dados = await response.json();
    console.log('Resultado:', dados);
    
    console.log(`\nValidando CEP inválido: ${cepInvalido}`);
    response = await fetch(`${API_BASE}/cep/validar/${cepInvalido}`);
    dados = await response.json();
    console.log('Resultado:', dados);
}

/**
 * Teste 4: Consultar CEP
 */
async function testarConsultaCep() {
    console.log('\n=== TESTE 4: Consulta de CEP ===');
    
    const cep = '01310100'; // Avenida Paulista, São Paulo
    console.log(`Consultando CEP: ${cep}`);
    
    try {
        const response = await fetch(`${API_BASE}/cep/${cep}`);
        const dados = await response.json();
        
        if (dados.erro) {
            console.error('❌ Erro:', dados.mensagem);
        } else {
            console.log('✓ Sucesso! Endereço obtido:');
            console.table({
                'CEP': dados.cep,
                'Logradouro': dados.logradouro,
                'Bairro': dados.bairro,
                'Cidade': dados.cidade,
                'Estado': dados.estado,
                'Latitude': dados.latitude,
                'Longitude': dados.longitude,
                'DDD': dados.ddd
            });
        }
    } catch (erro) {
        console.error('❌ Erro na requisição:', erro.message);
    }
}

/**
 * Teste 5: Preenchimento Automático por CNPJ
 */
async function testarPreenchimentoCnpj() {
    console.log('\n=== TESTE 5: Preenchimento Automático por CNPJ ===');
    
    const cnpj = '11222333000181';
    console.log(`Preenchendo formulário com CNPJ: ${cnpj}`);
    
    try {
        const response = await fetch(`${API_BASE}/preenchimento-automatico/cnpj/${cnpj}`);
        const dados = await response.json();
        
        if (!dados.sucesso) {
            console.error('❌ Erro:', dados.mensagem);
        } else {
            console.log('✓ Sucesso! Dados consolidados:');
            console.log('Dados CNPJ:', dados.dadosCnpj);
            if (dados.dadosCep) {
                console.log('Dados CEP:', dados.dadosCep);
            }
        }
    } catch (erro) {
        console.error('❌ Erro na requisição:', erro.message);
    }
}

/**
 * Teste 6: Preenchimento Automático por CEP
 */
async function testarPreenchimentoCep() {
    console.log('\n=== TESTE 6: Preenchimento Automático por CEP ===');
    
    const cep = '01310100';
    console.log(`Preenchendo formulário com CEP: ${cep}`);
    
    try {
        const response = await fetch(`${API_BASE}/preenchimento-automatico/cep/${cep}`);
        const dados = await response.json();
        
        if (!dados.sucesso) {
            console.error('❌ Erro:', dados.mensagem);
        } else {
            console.log('✓ Sucesso! Dados de CEP:');
            console.table(dados.dadosCep);
        }
    } catch (erro) {
        console.error('❌ Erro na requisição:', erro.message);
    }
}

/**
 * Teste 7: Carregamento de Categorias
 */
async function testarCarregamentoCategorias() {
    console.log('\n=== TESTE 7: Carregamento de Categorias ===');
    
    try {
        const response = await fetch(`${API_BASE}/categorias/ativas`);
        const categorias = await response.json();
        
        console.log(`✓ ${categorias.length} categorias carregadas`);
        console.table(categorias.slice(0, 5).map(c => ({ ID: c.id, Nome: c.nome, Ativa: c.ativa })));
        
        if (categorias.length === 0) {
            console.warn('⚠ Nenhuma categoria ativa encontrada. Inserir via POST /api/categorias');
        }
    } catch (erro) {
        console.error('❌ Erro ao carregar categorias:', erro.message);
    }
}

/**
 * Teste 8: Carregamento de Atividades
 */
async function testarCarregamentoAtividades() {
    console.log('\n=== TESTE 8: Carregamento de Atividades ===');
    
    try {
        const response = await fetch(`${API_BASE}/atividades/ativas`);
        const atividades = await response.json();
        
        console.log(`✓ ${atividades.length} atividades carregadas`);
        console.table(atividades.slice(0, 5).map(a => ({ ID: a.id, CNAE: a.cnae, Descrição: a.descricao })));
        
        if (atividades.length === 0) {
            console.warn('⚠ Nenhuma atividade ativa encontrada. Inserir via POST /api/atividades');
        }
    } catch (erro) {
        console.error('❌ Erro ao carregar atividades:', erro.message);
    }
}

/**
 * Teste 9: Criar Categoria de Teste
 */
async function testarCriaCategoriaSimples() {
    console.log('\n=== TESTE 9: Criar Categoria de Teste ===');
    
    const novaCategoria = {
        nome: `Teste ${Date.now()}`,
        descricao: 'Categoria de teste para validar API',
        icone: 'fa-box',
        ativa: true
    };
    
    console.log('Criando categoria:', novaCategoria);
    
    try {
        const response = await fetch(`${API_BASE}/categorias`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novaCategoria)
        });
        
        if (!response.ok) {
            console.error('❌ Erro HTTP:', response.status, response.statusText);
            return;
        }
        
        const dados = await response.json();
        console.log('✓ Categoria criada com sucesso!');
        console.table({
            'ID': dados.id,
            'Nome': dados.nome,
            'Descrição': dados.descricao,
            'Ativa': dados.ativa
        });
    } catch (erro) {
        console.error('❌ Erro ao criar categoria:', erro.message);
    }
}

/**
 * Teste 10: Criar Atividade de Teste
 */
async function testarCriaAtividadeSimples() {
    console.log('\n=== TESTE 10: Criar Atividade de Teste ===');
    
    const novaAtividade = {
        cnae: `${Date.now().toString().slice(-7)}`,
        descricao: `Atividade de Teste ${Date.now()}`,
        secao: 'A',
        divisao: '01',
        ativa: true
    };
    
    console.log('Criando atividade:', novaAtividade);
    
    try {
        const response = await fetch(`${API_BASE}/atividades`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novaAtividade)
        });
        
        if (!response.ok) {
            console.error('❌ Erro HTTP:', response.status, response.statusText);
            return;
        }
        
        const dados = await response.json();
        console.log('✓ Atividade criada com sucesso!');
        console.table({
            'ID': dados.id,
            'CNAE': dados.cnae,
            'Descrição': dados.descricao,
            'Seção': dados.secao,
            'Ativa': dados.ativa
        });
    } catch (erro) {
        console.error('❌ Erro ao criar atividade:', erro.message);
    }
}

/**
 * Teste 11: Verificar Compatibilidade de Scripts
 */
function testarCompatibilidadeScripts() {
    console.log('\n=== TESTE 11: Compatibilidade de Scripts ===');
    
    // Verifica se script de preenchimento foi carregado
    if (typeof consultarCnpj === 'function') {
        console.log('✓ Função consultarCnpj disponível');
    } else {
        console.error('❌ Função consultarCnpj não encontrada');
    }
    
    if (typeof consultarCep === 'function') {
        console.log('✓ Função consultarCep disponível');
    } else {
        console.error('❌ Função consultarCep não encontrada');
    }
    
    if (typeof carregarCategorias === 'function') {
        console.log('✓ Função carregarCategorias disponível');
    } else {
        console.error('❌ Função carregarCategorias não encontrada');
    }
    
    if (typeof carregarAtividades === 'function') {
        console.log('✓ Função carregarAtividades disponível');
    } else {
        console.error('❌ Função carregarAtividades não encontrada');
    }
}

/**
 * Executar todos os testes
 */
async function runAllTests() {
    console.clear();
    console.log('╔════════════════════════════════════════════╗');
    console.log('║   TESTES DE INTEGRAÇÃO - RADAR FORNECEDOR   ║');
    console.log('╚════════════════════════════════════════════╝');
    
    try {
        await testarValidacaoCnpj();
        await testarConsultaCnpj();
        await testarValidacaoCep();
        await testarConsultaCep();
        await testarPreenchimentoCnpj();
        await testarPreenchimentoCep();
        await testarCarregamentoCategorias();
        await testarCarregamentoAtividades();
        await testarCriaCategoriaSimples();
        await testarCriaAtividadeSimples();
        testarCompatibilidadeScripts();
        
        console.log('\n╔════════════════════════════════════════════╗');
        console.log('║           TESTES COMPLETOS!                 ║');
        console.log('╚════════════════════════════════════════════╝');
        
    } catch (erro) {
        console.error('❌ Erro geral:', erro);
    }
}

// Função auxiliar para testar função no formulário
function testarPreenchimentoFormulario() {
    console.log('\n=== TESTE MANUAL: Preenchimento de Formulário ===');
    console.log('1. Abra a página de formulário (/pages/fornecedores.html)');
    console.log('2. Clique em "NOVO FORNECEDOR"');
    console.log('3. No campo CNPJ, digite: 11222333000181');
    console.log('4. Pressione TAB');
    console.log('5. Observe se os campos foram preenchidos automaticamente');
    console.log('6. Se sim, clique em "SALVAR REGISTRO"');
    console.log('7. Verifique no Network se POST foi bem-sucedido');
}

console.log('Testes carregados! Execute: runAllTests()');
