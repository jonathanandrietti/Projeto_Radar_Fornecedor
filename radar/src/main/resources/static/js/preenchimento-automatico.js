/**
 * Script para preenchimento automático de formulários
 * Integra consultas de CNPJ, CEP e categorias
 */

// URLs das APIs
const API_BASE = 'http://localhost:8080/api';
const API_PREENCHIMENTO = `${API_BASE}/preenchimento-automatico`;
const API_CATEGORIAS = `${API_BASE}/categorias/ativas`;
const API_ATIVIDADES = `${API_BASE}/atividades/ativas`;

/**
 * Consulta CNPJ e preenche formulário automaticamente
 * @param {string} cnpj - CNPJ com ou sem formatação
 * @param {Function} callback - Função executada após sucesso
 */
async function consultarCnpj(cnpj) {
    try {
        // Remove formatação
        const cnpjLimpo = cnpj.replace(/\D/g, '');
        
        if (cnpjLimpo.length !== 14) {
            mostrarErro('CNPJ deve conter 14 dígitos');
            return false;
        }

        mostrarCarregamento('Consultando CNPJ...');
        
        const response = await fetch(`${API_PREENCHIMENTO}/cnpj/${cnpjLimpo}`);
        const dados = await response.json();
        
        if (!dados.sucesso) {
            mostrarErro(dados.mensagem || 'CNPJ não encontrado');
            return false;
        }

        // Preenche campos com dados do CNPJ
        preencherCamposComCnpj(dados.dadosCnpj);
        mostrarSucesso('Dados da empresa carregados com sucesso!');
        
        return true;

    } catch (erro) {
        console.error('Erro ao consultar CNPJ:', erro);
        mostrarErro('Erro ao consultar CNPJ: ' + erro.message);
        return false;
    }
}

/**
 * Preenche campos de formulário com dados do CNPJ
 * @param {Object} dados - Dados retornados da API
 */
function preencherCamposComCnpj(dados) {
    if (!dados) return;

    // Campos de identificação
    if (document.getElementById('nome')) {
        document.getElementById('nome').value = dados.nome || dados.razaoSocial || '';
    }
    if (document.getElementById('empresa')) {
        document.getElementById('empresa').value = dados.nome || dados.razaoSocial || '';
    }
    if (document.getElementById('nomeFantasia')) {
        document.getElementById('nomeFantasia').value = dados.nomeFantasia || '';
    }

    // Campos de endereço
    if (document.getElementById('logradouro')) {
        document.getElementById('logradouro').value = dados.logradouro || '';
    }
    if (document.getElementById('numero')) {
        document.getElementById('numero').value = dados.numero || '';
    }
    if (document.getElementById('complemento')) {
        document.getElementById('complemento').value = dados.complemento || '';
    }
    if (document.getElementById('bairro')) {
        document.getElementById('bairro').value = dados.bairro || '';
    }
    if (document.getElementById('cidade')) {
        document.getElementById('cidade').value = dados.cidade || '';
    }
    if (document.getElementById('estado')) {
        document.getElementById('estado').value = dados.estado || '';
    }
    if (document.getElementById('cep')) {
        document.getElementById('cep').value = dados.cep || '';
        // Se temos CEP, consulta localização
        if (dados.cep) {
            consultarCepAutomatico(dados.cep);
        }
    }

    // Campos de contato
    if (document.getElementById('email')) {
        document.getElementById('email').value = dados.email || '';
    }
    if (document.getElementById('telefone')) {
        document.getElementById('telefone').value = dados.telefone || '';
    }

    // Campos de atividade (se existem na página)
    // Tenta preencher atividade automaticamente via CNAE
    if (dados.cnae || dados.atividade) {
        preencherAtividadeAutomatica(dados.cnae, dados.atividade);
    }
    if (document.getElementById('cnae')) {
        document.getElementById('cnae').value = dados.cnae || '';
    }
}

/**
 * Preenche campo de atividade automaticamente tentando encontrar a atividade pelo CNAE
 * @param {string} cnae - Código CNAE da empresa
 * @param {string} nomeAtividade - Nome da atividade
 */
async function preencherAtividadeAutomatica(cnae, nomeAtividade) {
    const atividadeEl = document.getElementById('atividade');
    if (!atividadeEl) return;
    
    try {
        // Busca atividades ativas da API
        const response = await fetch(API_ATIVIDADES);
        const atividades = await response.json();
        
        // Tenta encontrar por CNAE primeiro
        if (cnae) {
            const encontrada = atividades.find(a => a.cnae === cnae || a.cnae === String(cnae).substring(0, 4));
            if (encontrada) {
                atividadeEl.value = encontrada.id;
                console.log('✓ Atividade preenchida automaticamente: ' + encontrada.descricao);
                return;
            }
        }
        
        // Se não encontrou por CNAE, tenta por nome
        if (nomeAtividade) {
            const encontrada = atividades.find(a => 
                a.descricao.toLowerCase().includes(nomeAtividade.toLowerCase())
            );
            if (encontrada) {
                atividadeEl.value = encontrada.id;
                console.log('✓ Atividade preenchida automaticamente: ' + encontrada.descricao);
                return;
            }
        }
        
        console.log('ℹ Atividade não encontrada na base. Deixe em branco ou selecione manualmente.');
        
    } catch (erro) {
        console.warn('Erro ao preencher atividade automaticamente:', erro);
    }
}

/**
 * Consulta CEP e preenche formulário automaticamente
 * @param {string} cep - CEP com ou sem formatação
 */
async function consultarCep(cep) {
    try {
        // Remove formatação
        const cepLimpo = cep.replace(/\D/g, '');
        
        if (cepLimpo.length !== 8) {
            mostrarErro('CEP deve conter 8 dígitos');
            return false;
        }

        mostrarCarregamento('Consultando CEP...');
        
        const response = await fetch(`${API_PREENCHIMENTO}/cep/${cepLimpo}`);
        const dados = await response.json();
        
        if (!dados.sucesso) {
            mostrarErro(dados.mensagem || 'CEP não encontrado');
            return false;
        }

        // Preenche campos com dados do CEP
        preencherCamposComCep(dados.dadosCep);
        mostrarSucesso('Endereço carregado com sucesso!');
        
        return true;

    } catch (erro) {
        console.error('Erro ao consultar CEP:', erro);
        mostrarErro('Erro ao consultar CEP: ' + erro.message);
        return false;
    }
}

/**
 * Versão interna para consulta automática de CEP (sem mensagens)
 */
async function consultarCepAutomatico(cep) {
    try {
        const cepLimpo = cep.replace(/\D/g, '');
        
        if (cepLimpo.length !== 8) return false;

        const response = await fetch(`${API_PREENCHIMENTO}/cep/${cepLimpo}`);
        const dados = await response.json();
        
        if (dados.sucesso && dados.dadosCep) {
            preencherCamposComCep(dados.dadosCep);
            return true;
        }
        return false;

    } catch (erro) {
        console.error('Erro ao consultar CEP automático:', erro);
        return false;
    }
}

/**
 * Preenche campos de formulário com dados do CEP
 * @param {Object} dados - Dados retornados da API
 */
function preencherCamposComCep(dados) {
    if (!dados) return;

    // Campos de endereço
    if (document.getElementById('logradouro')) {
        document.getElementById('logradouro').value = dados.logradouro || '';
    }
    if (document.getElementById('bairro')) {
        document.getElementById('bairro').value = dados.bairro || '';
    }
    if (document.getElementById('cidade')) {
        document.getElementById('cidade').value = dados.cidade || '';
    }
    if (document.getElementById('estado')) {
        document.getElementById('estado').value = dados.estado || '';
    }

    // Campos de geolocalização
    if (document.getElementById('latitude')) {
        document.getElementById('latitude').value = dados.latitude || '';
    }
    if (document.getElementById('longitude')) {
        document.getElementById('longitude').value = dados.longitude || '';
    }

    // Mostrar coordenadas no mapa (se houver)
    if (dados.latitude && dados.longitude) {
        mostrarNoMapa(dados.latitude, dados.longitude);
    }
}

/**
 * Carrega lista de categorias para um select
 * @param {string} elementId - ID do elemento select
 */
async function carregarCategorias(elementId = 'categoria') {
    try {
        const selectElement = document.getElementById(elementId);
        if (!selectElement) return;

        const response = await fetch(API_CATEGORIAS);
        const categorias = await response.json();

        // Limpa opções existentes
        selectElement.innerHTML = '<option value="">Selecione uma categoria...</option>';

        // Adiciona categorias
        categorias.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = `${cat.nome} ${cat.icone ? '- ' + cat.icone : ''}`;
            option.dataset.icone = cat.icone || '';
            selectElement.appendChild(option);
        });

        // Adiciona opção "Outros" com separador
        const separador = document.createElement('option');
        separador.disabled = true;
        separador.textContent = '───────────────';
        selectElement.appendChild(separador);

        const outrosOption = document.createElement('option');
        outrosOption.value = 'outros';
        outrosOption.textContent = '➕ Adicionar novo item';
        outrosOption.className = 'option-adicionar';
        selectElement.appendChild(outrosOption);

        // Listener para abrir modal ao selecionar "Outros"
        selectElement.addEventListener('change', function() {
            if (this.value === 'outros') {
                abrirModalOutros(elementId);
                this.value = ''; // Reset select
            }
        });

    } catch (erro) {
        console.error('Erro ao carregar categorias:', erro);
    }
}

/**
 * Carrega lista de atividades para um select
 * @param {string} elementId - ID do elemento select
 */
async function carregarAtividades(elementId = 'atividade') {
    try {
        const selectElement = document.getElementById(elementId);
        if (!selectElement) return;

        const response = await fetch(API_ATIVIDADES);
        const atividades = await response.json();

        // Limpa opções existentes
        selectElement.innerHTML = '<option value="">Selecione uma atividade...</option>';

        // Agrupa por seção
        const porSecao = {};
        atividades.forEach(at => {
            const secao = at.secao || 'Outras';
            if (!porSecao[secao]) porSecao[secao] = [];
            porSecao[secao].push(at);
        });

        // Adiciona grupos de atividades
        Object.keys(porSecao).sort().forEach(secao => {
            const optgroup = document.createElement('optgroup');
            optgroup.label = `Seção ${secao}`;
            
            porSecao[secao].forEach(at => {
                const option = document.createElement('option');
                option.value = at.id;
                option.textContent = `${at.cnae ? at.cnae + ' - ' : ''}${at.descricao}`;
                optgroup.appendChild(option);
            });
            
            selectElement.appendChild(optgroup);
        });

        // Adiciona opção "Adicionar novo item" com separador
        const separador = document.createElement('option');
        separador.disabled = true;
        separador.textContent = '───────────────';
        selectElement.appendChild(separador);

        const outrosOption = document.createElement('option');
        outrosOption.value = 'outros';
        outrosOption.textContent = '➕ Adicionar novo item';
        outrosOption.className = 'option-adicionar';
        selectElement.appendChild(outrosOption);

        // Listener para abrir modal ao selecionar "Outros"
        selectElement.addEventListener('change', function() {
            if (this.value === 'outros') {
                abrirModalOutros(elementId);
                this.value = ''; // Reset select
            }
        });

    } catch (erro) {
        console.error('Erro ao carregar atividades:', erro);
    }
}

/**
 * Integra evento onblur em campo CNPJ
 * @param {HTMLElement} element - Input element
 */
function integrarConsultaCnpj(element) {
    element.addEventListener('blur', async function() {
        const cnpj = this.value.trim();
        if (cnpj && cnpj.length >= 11) {
            await consultarCnpj(cnpj);
        }
    });
}

/**
 * Integra evento onblur em campo CEP
 * @param {HTMLElement} element - Input element
 */
function integrarConsultaCep(element) {
    element.addEventListener('blur', async function() {
        const cep = this.value.trim();
        if (cep && cep.length >= 7) {
            await consultarCep(cep);
        }
    });
}

/**
 * Mostra mensagem de sucesso
 * @param {string} mensagem - Mensagem a exibir
 */
function mostrarSucesso(mensagem) {
    console.log('✓ ' + mensagem);
    
    // Procura por elemento de mensagem de sucesso
    let alerta = document.querySelector('.alert-success');
    if (!alerta) {
        alerta = document.createElement('div');
        alerta.className = 'alert alert-success alert-dismissible fade show';
        alerta.role = 'alert';
        document.body.prepend(alerta);
    }
    
    alerta.innerHTML = `
        ${mensagem}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    alerta.style.display = 'block';
    
    // Remove após 5 segundos
    setTimeout(() => {
        alerta.style.display = 'none';
    }, 5000);
}

/**
 * Mostra mensagem de erro
 * @param {string} mensagem - Mensagem de erro
 */
function mostrarErro(mensagem) {
    console.error('✗ ' + mensagem);
    
    // Procura por elemento de mensagem de erro
    let alerta = document.querySelector('.alert-danger');
    if (!alerta) {
        alerta = document.createElement('div');
        alerta.className = 'alert alert-danger alert-dismissible fade show';
        alerta.role = 'alert';
        document.body.prepend(alerta);
    }
    
    alerta.innerHTML = `
        ${mensagem}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    alerta.style.display = 'block';
    
    // Remove após 5 segundos
    setTimeout(() => {
        alerta.style.display = 'none';
    }, 5000);
}

/**
 * Mostra indicador de carregamento
 * @param {string} mensagem - Mensagem de carregamento
 */
function mostrarCarregamento(mensagem) {
    console.log('⟳ ' + mensagem);
    
    // Procura por elemento de carregamento
    let alerta = document.querySelector('.alert-info');
    if (!alerta) {
        alerta = document.createElement('div');
        alerta.className = 'alert alert-info';
        alerta.role = 'alert';
        document.body.prepend(alerta);
    }
    
    alerta.innerHTML = `
        <span class="spinner-border spinner-border-sm me-2"></span> ${mensagem}
    `;
    alerta.style.display = 'block';
}

/**
 * Mostra localização em mapa (placeholder para futuras integrações)
 * @param {number} latitude - Latitude
 * @param {number} longitude - Longitude
 */
function mostrarNoMapa(latitude, longitude) {
    console.log(`📍 Localização: ${latitude}, ${longitude}`);
    
    // Procura por elemento de mapa
    const mapElement = document.getElementById('mapa');
    if (mapElement) {
        mapElement.innerHTML = `
            <div class="alert alert-info">
                <strong>Localização encontrada:</strong> ${latitude.toFixed(4)}, ${longitude.toFixed(4)}
                <br/>
                <small><a href="https://maps.google.com/?q=${latitude},${longitude}" target="_blank">
                    Ver no Google Maps
                </a></small>
            </div>
        `;
    }
}

/**
 * Inicializa todos os campos de preenchimento automático na página
 */
function inicializarPreenchimentoAutomatico() {
    // Campos CNPJ
    const campoCnpj = document.getElementById('cnpj');
    if (campoCnpj) {
        integrarConsultaCnpj(campoCnpj);
    }

    // Campos CEP
    const campoCep = document.getElementById('cep');
    if (campoCep) {
        integrarConsultaCep(campoCep);
    }

    // Carrega listas de categorias e atividades
    carregarCategorias('categoria');
    carregarAtividades('atividade');

    console.log('✓ Preenchimento automático inicializado');
}

/**
 * Abre modal inteligente "Outros" que pergunta se quer adicionar Categoria ou Atividade
 */
function abrirModalOutros(selectElementId) {
    let modal = document.getElementById('modal-dialog-outros');
    if (!modal) {
        modal = criarModalDialogoOutros();
        document.body.appendChild(modal);
    }
    
    modal.dataset.selectId = selectElementId;
    modal.classList.remove('hidden');
}

/**
 * Fecha modal de diálogo "Outros"
 */
function fecharModalOutros() {
    const modal = document.getElementById('modal-dialog-outros');
    if (modal) {
        modal.classList.add('hidden');
    }
}

/**
 * Cria estrutura HTML do modal de diálogo "Outros"
 */
function criarModalDialogoOutros() {
    const modal = document.createElement('div');
    modal.id = 'modal-dialog-outros';
    modal.className = 'fixed inset-0 z-[100] hidden overflow-y-auto bg-slate-950/80 backdrop-blur-sm p-4 flex items-center justify-center';
    
    modal.innerHTML = `
        <div class="mx-auto max-w-sm rounded-2xl bg-white shadow-2xl">
            <div class="border-b px-8 py-5 bg-amber-50 rounded-t-2xl">
                <h2 class="text-lg font-black text-slate-800 uppercase tracking-tight">📌 O que deseja cadastrar?</h2>
            </div>
            <div class="p-8 space-y-4">
                <p class="text-slate-600 text-sm mb-6">Escolha uma opção para adicionar um novo item:</p>
                
                <button onclick="abrirNovaCategoria()" class="w-full rounded-xl bg-sky-50 border-2 border-sky-200 px-6 py-4 text-left hover:bg-sky-100 hover:border-sky-400 transition-all">
                    <div class="font-black text-sky-700 mb-1">📦 Nova Categoria</div>
                    <div class="text-sm text-sky-600">Adicionar tipo de produto/serviço</div>
                </button>
                
                <button onclick="abrirNovaAtividadeFromOutros()" class="w-full rounded-xl bg-amber-50 border-2 border-amber-200 px-6 py-4 text-left hover:bg-amber-100 hover:border-amber-400 transition-all">
                    <div class="font-black text-amber-700 mb-1">🏢 Nova Atividade (CNAE)</div>
                    <div class="text-sm text-amber-600">Adicionar código de atividade</div>
                </button>
                
                <button onclick="fecharModalOutros()" class="w-full rounded-xl border-2 border-slate-200 px-6 py-4 text-center font-bold text-slate-600 hover:bg-slate-50 transition-all mt-6">
                    Cancelar
                </button>
            </div>
        </div>
    `;
    
    return modal;
}

/**
 * Abre modal de nova categoria após clicar em "Outros"
 */
async function abrirNovaCategoria() {
    fecharModalOutros();
    const selectId = document.getElementById('modal-dialog-outros').dataset.selectId;
    abrirModalNovaCategoria(selectId);
}

/**
 * Abre modal de nova atividade após clicar em "Outros"
 */
async function abrirNovaAtividadeFromOutros() {
    fecharModalOutros();
    const selectId = document.getElementById('modal-dialog-outros').dataset.selectId;
    abrirModalNovaAtividade(selectId);
}

/**
 * Abre modal para adicionar nova categoria
 */
function abrirModalNovaCategoria(selectElementId) {
    // Cria modal se não existir
    let modal = document.getElementById('modal-nova-categoria');
    if (!modal) {
        modal = criarModalCategoria();
        document.body.appendChild(modal);
    }
    
    // Armazena o ID do select para preencher depois
    modal.dataset.selectId = selectElementId;
    
    // Limpa formulário
    document.getElementById('form-nova-categoria').reset();
    
    // Mostra modal
    modal.classList.remove('hidden');
}

/**
 * Fecha modal de nova categoria
 */
function fecharModalNovaCategoria() {
    const modal = document.getElementById('modal-nova-categoria');
    if (modal) {
        modal.classList.add('hidden');
    }
}

/**
 * Abre modal para adicionar nova atividade
 */
function abrirModalNovaAtividade(selectElementId) {
    // Cria modal se não existir
    let modal = document.getElementById('modal-nova-atividade');
    if (!modal) {
        modal = criarModalAtividade();
        document.body.appendChild(modal);
    }
    
    // Armazena o ID do select para preencher depois
    modal.dataset.selectId = selectElementId;
    
    // Limpa formulário
    document.getElementById('form-nova-atividade').reset();
    
    // Mostra modal
    modal.classList.remove('hidden');
}

/**
 * Fecha modal de nova atividade
 */
function fecharModalNovaAtividade() {
    const modal = document.getElementById('modal-nova-atividade');
    if (modal) {
        modal.classList.add('hidden');
    }
}

/**
 * Cria estrutura HTML do modal de atividade
 */
function criarModalAtividade() {
    const modal = document.createElement('div');
    modal.id = 'modal-nova-atividade';
    modal.className = 'fixed inset-0 z-[100] hidden overflow-y-auto bg-slate-950/80 backdrop-blur-sm p-4';
    
    modal.innerHTML = `
        <div class="mx-auto my-8 max-w-md rounded-2xl bg-white shadow-2xl">
            <div class="flex items-center justify-between border-b px-8 py-5 bg-amber-50 rounded-t-2xl">
                <h2 class="text-lg font-black text-slate-800 uppercase tracking-tight">Nova Atividade (CNAE)</h2>
                <button onclick="fecharModalNovaAtividade()" class="text-2xl text-slate-400 hover:text-slate-600">&times;</button>
            </div>
            <form id="form-nova-atividade" class="space-y-5 p-8" onsubmit="salvarNovaAtividade(event)">
                <label class="campo block">
                    <span class="font-black text-[10px] text-slate-400 uppercase">Código CNAE *</span>
                    <input id="atividade-cnae" type="text" required maxlength="10" class="mt-2 w-full rounded-xl border border-slate-200 px-4 py-3 focus:ring-2 focus:ring-sky-500 outline-none" placeholder="Ex: 6201">
                </label>
                
                <label class="campo block">
                    <span class="font-black text-[10px] text-slate-400 uppercase">Descrição da Atividade *</span>
                    <input id="atividade-descricao" type="text" required maxlength="255" class="mt-2 w-full rounded-xl border border-slate-200 px-4 py-3 focus:ring-2 focus:ring-sky-500 outline-none" placeholder="Ex: Atividades de consultoria">
                </label>
                
                <label class="campo block">
                    <span class="font-black text-[10px] text-slate-400 uppercase">Seção</span>
                    <input id="atividade-secao" type="text" maxlength="5" class="mt-2 w-full rounded-xl border border-slate-200 px-4 py-3 focus:ring-2 focus:ring-sky-500 outline-none" placeholder="Ex: M">
                </label>
                
                <label class="campo block">
                    <span class="font-black text-[10px] text-slate-400 uppercase">Divisão</span>
                    <input id="atividade-divisao" type="text" maxlength="5" class="mt-2 w-full rounded-xl border border-slate-200 px-4 py-3 focus:ring-2 focus:ring-sky-500 outline-none" placeholder="Ex: 62">
                </label>
                
                <div class="flex justify-end gap-4 pt-4">
                    <button type="button" onclick="fecharModalNovaAtividade()" class="rounded-xl border border-slate-200 px-6 py-3 font-bold text-slate-500 hover:bg-slate-50">CANCELAR</button>
                    <button type="submit" class="rounded-xl bg-sky-600 px-8 py-3 font-black text-white shadow-lg shadow-sky-100 hover:bg-sky-700">ADICIONAR</button>
                </div>
            </form>
        </div>
    `;
    
    return modal;
}

/**
 * Salva nova atividade
 */
async function salvarNovaAtividade(event) {
    event.preventDefault();
    
    const cnae = document.getElementById('atividade-cnae').value.trim();
    const descricao = document.getElementById('atividade-descricao').value.trim();
    const secao = document.getElementById('atividade-secao').value.trim();
    const divisao = document.getElementById('atividade-divisao').value.trim();
    
    if (!cnae || !descricao) {
        alert('Código CNAE e Descrição são obrigatórios');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/atividades`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ cnae, descricao, secao, divisao, ativa: true })
        });
        
        if (!response.ok) {
            throw new Error('Erro ao salvar atividade');
        }
        
        const novaAtividade = await response.json();
        mostrarSucesso('Atividade adicionada com sucesso!');
        
        // Recarrega lista de atividades no select
        const selectId = document.getElementById('modal-nova-atividade').dataset.selectId;
        await carregarAtividades(selectId);
        
        // Seleciona a nova atividade no select
        const selectElement = document.getElementById(selectId);
        if (selectElement) {
            selectElement.value = novaAtividade.id;
        }
        
        fecharModalNovaAtividade();
        
    } catch (erro) {
        console.error('Erro ao salvar atividade:', erro);
        mostrarErro('Erro ao salvar atividade: ' + erro.message);
    }
}

// Inicializa quando o DOM está pronto
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', inicializarPreenchimentoAutomatico);
} else {
    inicializarPreenchimentoAutomatico();
}
