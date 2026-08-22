// Global state
let currentMode = 'fornecedores'; // 'fornecedores' or 'compradores'
let currentDataList = [];
let editingId = null; // Control ID for editing

// On document load
document.addEventListener('DOMContentLoaded', () => {
    mudarModo('fornecedores');
});

// Toggle active visual state in menu and refresh content
function mudarModo(mode) {
    currentMode = mode;
    editingId = null;
    const menuFornecedores = document.getElementById('menu-fornecedores');
    const menuCompradores = document.getElementById('menu-compradores');
    
    // Adjust visual classes of sidebar buttons using our custom CSS classes
    if (mode === 'fornecedores') {
        menuFornecedores.className = "w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition font-semibold btn-sidebar-active";
        menuCompradores.className = "w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition font-semibold btn-sidebar-inactive";
        
        // Adjust panels text
        document.getElementById('main-panel-title').innerText = "Painel de Monitoramento: Fornecedores";
        document.getElementById('main-panel-subtitle').innerText = "Análise de risco, conformidade e localização de empresas fornecedoras.";
        document.getElementById('kpi-label-total').innerText = "Total de Fornecedores";
        document.getElementById('kpi-icon-total').className = "fa-solid fa-truck-field text-2xl";
        document.getElementById('btn-novo-registro').innerHTML = `<i class="fa-solid fa-plus text-base"></i> Novo Fornecedor`;
        document.getElementById('table-title').innerText = "Fornecedores Cadastrados";
        document.getElementById('table-icon').className = "fa-solid fa-list text-primary-500";
    } else {
        menuFornecedores.className = "w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition font-semibold btn-sidebar-inactive";
        menuCompradores.className = "w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition font-semibold btn-sidebar-active";
        
        // Adjust panels text
        document.getElementById('main-panel-title').innerText = "Painel de Monitoramento: Compradores";
        document.getElementById('main-panel-subtitle').innerText = "Análise de risco, conformidade e localização de empresas compradoras.";
        document.getElementById('kpi-label-total').innerText = "Total de Compradores";
        document.getElementById('kpi-icon-total').className = "fa-solid fa-cart-shopping text-2xl";
        document.getElementById('btn-novo-registro').innerHTML = `<i class="fa-solid fa-plus text-base"></i> Novo Comprador`;
        document.getElementById('table-title').innerText = "Compradores Cadastrados";
        document.getElementById('table-icon').className = "fa-solid fa-cart-shopping text-primary-500";
    }

    carregarDados();
}

// Fetch API dynamic loader
function getApiUrl() {
    return currentMode === 'fornecedores' ? '/api/fornecedores' : '/api/compradores';
}

// Load data dynamically
async function carregarDados() {
    const tbody = document.getElementById('dados-tbody');
    mostrarLoading(tbody);

    try {
        const url = getApiUrl();
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Falha ao se comunicar com o servidor.');
        }
        currentDataList = await response.json();
        renderizarTabela(currentDataList);
        atualizarPainelKPIs(currentDataList);
    } catch (error) {
        console.error(error);
        mostrarAlerta('Erro', 'Não foi possível carregar os dados: ' + error.message, 'error');
        mostrarErroTabela(tbody, error.message);
    }
}

// Fetch address from ViaCEP API
async function buscarCep() {
    const cepInput = document.getElementById('form-cep');
    const cep = cepInput.value.replace(/\D/g, '');
    if (cep.length !== 8) {
        mostrarAlerta('Aviso', 'Digite um CEP com 8 dígitos para buscar.', 'warning');
        return;
    }
    try {
        mostrarAlerta('Buscando', 'Consultando CEP...', 'info');
        const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
        if (!response.ok) throw new Error();
        const data = await response.json();
        if (data.erro) {
            mostrarAlerta('Erro', 'CEP não localizado.', 'error');
            return;
        }
        document.getElementById('form-logradouro').value = data.logradouro || '';
        document.getElementById('form-bairro').value = data.bairro || '';
        document.getElementById('form-cidade').value = data.localidade || '';
        document.getElementById('form-estado').value = data.uf || '';
        mostrarAlerta('Sucesso', 'Endereço carregado automaticamente via CEP!', 'success');
    } catch (e) {
        mostrarAlerta('Erro', 'Não foi possível buscar o CEP. Digite manualmente.', 'error');
    }
}

// Search direct by CNPJ using backend API
async function buscarCnpjDireto() {
    const inputCnpj = document.getElementById('search-cnpj-direct');
    const cnpjLimpo = inputCnpj.value.replace(/\D/g, '');

    if (!cnpjLimpo) {
        mostrarAlerta('Aviso', 'Digite um CNPJ para buscar.', 'warning');
        return;
    }

    if (cnpjLimpo.length !== 14) {
        mostrarAlerta('Erro de Validação', 'O CNPJ deve conter exatamente 14 dígitos.', 'error');
        return;
    }

    const tbody = document.getElementById('dados-tbody');
    mostrarLoading(tbody);

    try {
        const url = getApiUrl();
        const response = await fetch(`${url}/cnpj/${cnpjLimpo}`);
        if (response.status === 404) {
            mostrarAlerta('Não Encontrado', `Nenhuma empresa encontrada com o CNPJ informado nesta categoria.`, 'warning');
            renderizarTabela([]);
            return;
        }
        if (!response.ok) {
            throw new Error('Falha na resposta do servidor.');
        }
        const entidade = await response.json();
        renderizarTabela([entidade]);
        mostrarAlerta('Sucesso', 'Empresa localizada com sucesso via CNPJ!', 'success');
    } catch (error) {
        console.error(error);
        mostrarAlerta('Erro', 'Não foi possível buscar o CNPJ: ' + error.message, 'error');
        mostrarErroTabela(tbody, error.message);
    }
}

// Handle Create/Update form submission
async function enviarDados(event) {
    event.preventDefault();

    const nomeInput = document.getElementById('form-nome').value.trim();
    const cnpjInput = document.getElementById('form-cnpj').value;
    const statusInput = document.getElementById('form-status').value;
    const riscoInput = parseFloat(document.getElementById('form-risco').value) || 0.0;

    const cep = document.getElementById('form-cep').value.replace(/\D/g, '');
    const logradouro = document.getElementById('form-logradouro').value.trim();
    const numero = document.getElementById('form-numero').value.trim();
    const complemento = document.getElementById('form-complemento').value.trim();
    const bairro = document.getElementById('form-bairro').value.trim();
    const cidade = document.getElementById('form-cidade').value.trim();
    const estado = document.getElementById('form-estado').value.trim().toUpperCase();
    const latVal = document.getElementById('form-latitude').value;
    const lngVal = document.getElementById('form-longitude').value;
    const latitude = latVal !== "" ? parseFloat(latVal) : null;
    const longitude = lngVal !== "" ? parseFloat(lngVal) : null;

    const cnpjLimpo = cnpjInput.replace(/\D/g, '');

    if (!nomeInput) {
        mostrarAlerta('Validação', 'Por favor, preencha o Nome da Empresa.', 'warning');
        return;
    }

    if (cnpjLimpo.length !== 14) {
        mostrarAlerta('Validação', 'O CNPJ deve conter exatamente 14 dígitos numéricos.', 'warning');
        return;
    }

    const payload = {
        nome: nomeInput,
        cnpj: cnpjLimpo,
        status: statusInput,
        pontuacaoRisco: riscoInput,
        cep, logradouro, numero, complemento, bairro, cidade, estado, latitude, longitude
    };

    const btnSubmit = document.getElementById('btn-submit');
    const originalBtnHtml = btnSubmit.innerHTML;
    btnSubmit.disabled = true;
    btnSubmit.innerHTML = `<i class="fa-solid fa-circle-notch fa-spin mr-1.5"></i> Gravando...`;

    try {
        const url = getApiUrl() + (editingId ? `/${editingId}` : '');
        const method = editingId ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.status === 400) {
            const errData = await response.json();
            const msg = (errData.errors && errData.errors[0]?.defaultMessage) || errData.message || 'Dados inválidos.';
            throw new Error(msg);
        }

        if (!response.ok) {
            const text = await response.text();
            throw new Error(JSON.parse(text).message || 'Erro ao salvar no servidor.');
        }

        const entidade = await response.json();
        mostrarAlerta('Sucesso', `Cadastro de "${entidade.nome}" ${editingId ? 'atualizado' : 'realizado'} com sucesso!`, 'success');
        closeModal();
        carregarDados();
    } catch (error) {
        console.error(error);
        mostrarAlerta('Erro ao Salvar', error.message, 'error');
    } finally {
        btnSubmit.disabled = false;
        btnSubmit.innerHTML = originalBtnHtml;
    }
}

// Handle Delete operation
async function excluirItem(id) {
    if (!confirm('Você tem certeza que deseja excluir este item? Esta ação é irreversível.')) {
        return;
    }

    try {
        const url = `${getApiUrl()}/${id}`;
        const response = await fetch(url, { method: 'DELETE' });

        if (!response.ok) {
            throw new Error('Falha ao excluir o item.');
        }
        
        mostrarAlerta('Sucesso', 'Item excluído com sucesso!', 'success');
        carregarDados();
    } catch (error) {
        console.error(error);
        mostrarAlerta('Erro', `Não foi possível excluir: ${error.message}`, 'error');
    }
}

// Render data list into the table
function renderizarTabela(dados) {
    const tbody = document.getElementById('dados-tbody');
    const countBadge = document.getElementById('table-count');
    
    tbody.innerHTML = '';
    countBadge.innerText = `${dados.length} ${dados.length === 1 ? 'item' : 'itens'}`;

    if (dados.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" class="py-12 text-center text-gray-400"><div class="flex flex-col items-center justify-center space-y-2"><i class="fa-solid fa-folder-open text-3xl text-gray-300"></i><p class="font-medium">Nenhum registro encontrado.</p></div></td></tr>`;
        return;
    }

    dados.forEach(f => {
        const tr = document.createElement('tr');
        tr.className = "hover:bg-gray-100/40 transition duration-150 border-b border-gray-200/50 last:border-none";
        
        const cnpjFormatado = formatarCNPJ(f.cnpj);
        const { badgeClass, statusNome } = getStatusBadge(f.status);
        const { riscoColor, riscoBg, riscoTexto, riscoClass, percentRisco } = getRiscoVisuals(f.pontuacaoRisco);
        const mapButton = getMapButton(f);

        tr.innerHTML = `
            <td class="py-3 px-5 font-semibold text-gray-500">${f.id}</td>
            <td class="py-3 px-5 font-bold text-gray-900">${f.nome}</td>
            <td class="py-3 px-5 font-mono text-gray-600">${cnpjFormatado}</td>
            <td class="py-3 px-5">
                <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold ${badgeClass}">
                    <span class="w-1.5 h-1.5 rounded-full mr-1.5 bg-current"></span>${statusNome}
                </span>
            </td>
            <td class="py-3 px-5">
                <div class="flex items-center space-x-3">
                    <div class="w-full bg-gray-200 rounded-full h-2 max-w-[100px]"><div class="${riscoColor} h-2 rounded-full" style="width: ${percentRisco}%"></div></div>
                    <span class="text-xs font-bold ${riscoTexto} px-2 py-0.5 rounded ${riscoBg}">${(f.pontuacaoRisco || 0).toFixed(1)} (${riscoClass})</span>
                </div>
            </td>
            <td class="py-3 px-5">${mapButton}</td>
            <td class="py-3 px-5 text-center">
                <button onclick="abrirModalEdicao(${f.id})" class="text-sky-600 hover:text-sky-800 transition p-1" title="Editar"><i class="fa-solid fa-pencil"></i></button>
                <button onclick="excluirItem(${f.id})" class="text-rose-500 hover:text-rose-700 transition p-1 ml-2" title="Excluir"><i class="fa-solid fa-trash-can"></i></button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Update KPIs dashboard
function atualizarPainelKPIs(dados) {
    const total = dados.length;
    const analise = dados.filter(f => f.status === 'EM_ANALISE').length;
    
    let somaRisco = 0, criticos = 0;
    dados.forEach(f => {
        const risco = f.pontuacaoRisco || 0;
        somaRisco += risco;
        if (risco >= 7.0) criticos++;
    });

    document.getElementById('stat-total').innerText = total;
    document.getElementById('stat-analise').innerText = analise;
    document.getElementById('stat-risco').innerText = total > 0 ? (somaRisco / total).toFixed(1) : '0.0';
    document.getElementById('stat-criticos').innerText = criticos;
}

// Client-side quick filter
function filterTable() {
    const query = document.getElementById('search-input').value.toLowerCase().trim();
    const filtrados = !query ? currentDataList : currentDataList.filter(f => 
        (f.nome || '').toLowerCase().includes(query) || (f.cnpj || '').toLowerCase().includes(query)
    );
    renderizarTabela(filtrados);
}

// UTILITY HELPERS
function formatarCNPJ(cnpj) {
    const limpo = (cnpj || '').replace(/\D/g, '');
    return limpo.length === 14 ? limpo.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, "$1.$2.$3/$4-$5") : cnpj;
}

function aplicarMascaraCNPJ(input) {
    let v = input.value.replace(/\D/g, '').slice(0, 14);
    v = v.replace(/^(\d{2})(\d)/, "$1.$2").replace(/^(\d{2}\.\d{3})(\d)/, "$1.$2").replace(/(\d{3}\.)(\d{3})(\d)/, "$1$2/$3").replace(/(\d{4}\/)(\d{2})$/, "$1$2");
    input.value = v;
}

function getStatusBadge(status) {
    const badges = {
        'APROVADO': { class: "bg-emerald-100 text-emerald-800 border-emerald-200", name: "Aprovado" },
        'EM_ANALISE': { class: "bg-amber-100 text-amber-800 border-amber-200", name: "Em Análise" },
        'REJEITADO': { class: "bg-rose-100 text-rose-800 border-rose-200", name: "Rejeitado" },
        'SUSPENSO': { class: "bg-gray-100 text-gray-800 border-gray-300", name: "Suspenso" }
    };
    const fallback = badges['EM_ANALISE'];
    const config = badges[status] || fallback;
    return { badgeClass: config.class, statusNome: config.name };
}

function getRiscoVisuals(risco) {
    risco = risco || 0;
    let config = { color: "bg-emerald-500", bg: "bg-emerald-50", texto: "text-emerald-700", class: "Baixo" };
    if (risco >= 7.0) config = { color: "bg-rose-500", bg: "bg-rose-50", texto: "text-rose-700", class: "Alto" };
    else if (risco >= 3.0) config = { color: "bg-amber-500", bg: "bg-amber-50", texto: "text-amber-700", class: "Médio" };
    return { ...config, percentRisco: Math.min(100, (risco / 10) * 100) };
}

function getMapButton(f) {
    let localSpan = "", localLink = "";
    if (f.cidade && f.estado) {
        localSpan = `${f.cidade}/${f.estado}`;
        let query = `${f.logradouro || ''}, ${f.numero || ''} - ${f.bairro || ''}, ${f.cidade} - ${f.estado}`;
        localLink = f.latitude && f.longitude ? `https://www.google.com/maps/search/?api=1&query=${f.latitude},${f.longitude}` : `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(query)}`;
    } else if (f.logradouro) {
        localSpan = f.logradouro;
        localLink = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(f.logradouro)}`;
    }
    return localLink ? `<div class="flex flex-col"><span class="font-semibold text-gray-800">${localSpan}</span><a href="${localLink}" target="_blank" class="text-xs text-primary-600 hover:text-primary-800 flex items-center gap-1 font-semibold mt-0.5 transition"><i class="fa-solid fa-map-pin text-primary-500"></i> Ver no Mapa</a></div>` : `<span class="text-xs text-gray-400 italic flex items-center gap-1"><i class="fa-solid fa-location-dot"></i> Não cadastrado</span>`;
}

// MODAL & ALERT HELPERS
function mostrarLoading(element) {
    element.innerHTML = `<tr><td colspan="7" class="py-12 text-center text-gray-400"><div class="flex flex-col items-center justify-center space-y-2"><i class="fa-solid fa-circle-notch fa-spin text-3xl text-sky-500"></i><p class="font-medium">Carregando dados...</p></div></td></tr>`;
}

function mostrarErroTabela(element, mensagem) {
    element.innerHTML = `<tr><td colspan="7" class="py-12 text-center text-rose-500 bg-rose-50/30"><div class="flex flex-col items-center justify-center space-y-2"><i class="fa-solid fa-triangle-exclamation text-3xl"></i><p class="font-bold">Erro ao carregar tabela</p><p class="text-xs max-w-md">${mensagem}</p></div></td></tr>`;
}

function mostrarAlerta(titulo, mensagem, tipo = 'success') {
    const container = document.getElementById('alert-container');
    const box = document.getElementById('alert-box');
    const icon = document.getElementById('alert-icon');
    const title = document.getElementById('alert-title');
    const message = document.getElementById('alert-message');

    container.classList.remove('hidden');
    box.className = "p-4 rounded-lg flex items-start space-x-3 shadow border";
    
    const types = {
        success: { class: 'bg-emerald-50 text-emerald-800 border-emerald-200', icon: 'fa-circle-check' },
        error: { class: 'bg-rose-50 text-rose-800 border-rose-200', icon: 'fa-circle-exclamation' },
        warning: { class: 'bg-amber-50 text-amber-800 border-amber-200', icon: 'fa-triangle-exclamation' },
        info: { class: 'bg-blue-50 text-blue-800 border-blue-200', icon: 'fa-circle-info' }
    };
    const config = types[tipo] || types['info'];

    box.classList.add(...config.class.split(' '));
    icon.innerHTML = `<i class="fa-solid ${config.icon} text-${config.class.split(' ')[1].split('-')[0]}-500"></i>`;
    title.innerText = titulo;
    message.innerText = mensagem;

    if (window.alertTimeout) clearTimeout(window.alertTimeout);
    window.alertTimeout = setTimeout(dismissAlert, 6000);
}

function dismissAlert() {
    document.getElementById('alert-container').classList.add('hidden');
}

function abrirModalEdicao(id) {
    const item = currentDataList.find(i => i.id === id);
    if (!item) return;

    editingId = id;
    openModal(true);

    // Populate form
    document.getElementById('form-nome').value = item.nome;
    document.getElementById('form-cnpj').value = formatarCNPJ(item.cnpj);
    document.getElementById('form-status').value = item.status || 'EM_ANALISE';
    document.getElementById('form-risco').value = item.pontuacaoRisco || 0.0;
    document.getElementById('form-cep').value = item.cep || '';
    document.getElementById('form-logradouro').value = item.logradouro || '';
    document.getElementById('form-numero').value = item.numero || '';
    document.getElementById('form-complemento').value = item.complemento || '';
    document.getElementById('form-bairro').value = item.bairro || '';
    document.getElementById('form-cidade').value = item.cidade || '';
    document.getElementById('form-estado').value = item.estado || '';
    document.getElementById('form-latitude').value = item.latitude || '';
    document.getElementById('form-longitude').value = item.longitude || '';
}

function openModal(isEditing = false) {
    editingId = isEditing ? editingId : null;
    const modal = document.getElementById('register-modal');
    const title = document.getElementById('modal-title-text');
    const btnText = document.getElementById('btn-submit-text');
    const type = currentMode === 'fornecedores' ? 'Fornecedor' : 'Comprador';
    const icon = currentMode === 'fornecedores' ? 'fa-truck-ramp-box' : 'fa-cart-shopping';

    title.innerHTML = `<i class="fa-solid ${icon}"></i> ${isEditing ? 'Editar' : 'Cadastrar'} ${type}`;
    btnText.innerText = isEditing ? `Salvar Alterações` : `Gravar ${type}`;

    if (!isEditing) document.getElementById('cadastro-form').reset();
    
    modal.classList.remove('hidden');
    setTimeout(() => {
        modal.classList.remove('opacity-0');
        modal.querySelector('.bg-white').classList.remove('scale-95');
    }, 50);
}

function closeModal() {
    editingId = null;
    const modal = document.getElementById('register-modal');
    modal.classList.add('opacity-0');
    modal.querySelector('.bg-white').classList.add('scale-95');
    setTimeout(() => modal.classList.add('hidden'), 300);
}
