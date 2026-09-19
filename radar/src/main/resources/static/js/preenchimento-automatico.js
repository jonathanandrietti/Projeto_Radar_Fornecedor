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
    if (document.getElementById('atividade')) {
        document.getElementById('atividade').value = dados.atividade || '';
    }
    if (document.getElementById('cnae')) {
        document.getElementById('cnae').value = dados.cnae || '';
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

// Inicializa quando o DOM está pronto
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', inicializarPreenchimentoAutomatico);
} else {
    inicializarPreenchimentoAutomatico();
}
