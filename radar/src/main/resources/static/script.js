console.log('script.js carregado!');

const configuracao = document.body.dataset;
const endpoint = `/api/${configuracao.entidade}`;
let registros = [];
let ultimoCnpjBuscado = '';

// Configuração de inatividade (2 minutos em milissegundos - reduzido para testes)
const INATIVIDADE_TIMEOUT_MS = 2 * 60 * 1000;
let tempoInatividade;
let ultimaAtividade = Date.now();

// Função para resetar o timer de inatividade
function resetarTimerInatividade() {
    const agora = Date.now();
    const tempoDecorrido = agora - ultimaAtividade;
    ultimaAtividade = agora;
    
    // Só limpa se já existir timeout pendente
    if (tempoInatividade) {
        clearTimeout(tempoInatividade);
    }
    
    // Define o novo timeout
    tempoInatividade = setTimeout(() => {
        logarAutomaticamente("Você ficou inativo por mais de 2 minutos.");
    }, INATIVIDADE_TIMEOUT_MS);
}

// Função para logout automático
async function logarAutomaticamente(mensagem) {
    try {
        await fetch('/api/login/logout', { method: 'POST' });
    } catch (e) {
        console.error('Erro ao encerrar sessão:', e);
    } finally {
        localStorage.removeItem('usuario');
        console.log(mensagem);
        alert(mensagem + ' Faça login novamente.');
        window.location.href = '/login.html';
    }
}

// Monitoramento de atividades do usuário
function monitorarAtividade() {
    resetarTimerInatividade();
}

// Adicionar listeners para diferentes tipos de interação
document.addEventListener('mousemove', monitorarAtividade);
document.addEventListener('keydown', monitorarAtividade);
document.addEventListener('click', monitorarAtividade);
document.addEventListener('touchstart', monitorarAtividade);
document.addEventListener('scroll', monitorarAtividade);

async function logout() {
    try {
        // Limpa o timeout se existir
        if (tempoInatividade) {
            clearTimeout(tempoInatividade);
        }
        await fetch('/api/login/logout', { method: 'POST' });
    } catch (e) {
        console.error('Erro ao encerrar sessão:', e);
    } finally {
        localStorage.removeItem('usuario');
        window.location.href = '/login.html';
    }
}

function exibirInfoUsuario() {
    const usuarioJson = localStorage.getItem('usuario');
    const container = document.getElementById('user-info');
    console.log('exibirInfoUsuario - usuarioJson:', usuarioJson);
    if (usuarioJson && container) {
        try {
            const user = JSON.parse(usuarioJson);
            console.log('User object:', user);
            container.innerHTML = `
                <span class="text-xs text-slate-400">Olá, <strong class="text-white">${user.nome || user.username}</strong> (${user.tipo || 'Usuário'})</span>
                <button onclick="logout()" class="rounded-lg bg-rose-600/20 px-3 py-1.5 text-xs font-bold text-rose-300 hover:bg-rose-600 hover:text-white transition" title="Sair / Trocar Acesso">
                    <i class="fa-solid fa-right-from-bracket mr-1"></i> Sair
                </button>
            `;
        } catch (e) {
            console.error('Erro ao parsear usuário:', e);
        }
    } else {
        console.log('Container não encontrado ou usuário não está no localStorage');
    }
}

function aplicarControlesDeAcesso() {
    try {
        const usuarioJson = localStorage.getItem('usuario');
        if (!usuarioJson) return;

        const user = JSON.parse(usuarioJson);
        const tipo = user.tipo;

        // 1. Controlar links na Sidebar
        const links = document.querySelectorAll('aside nav a');
        links.forEach(link => {
            const href = link.getAttribute('href');
            if (href) {
                if (href.includes('admin.html') && tipo !== 'ADMIN') {
                    link.style.display = 'none';
                }
                if (href.includes('clientes.html') && tipo === 'COMPRADOR') {
                    link.style.display = 'none';
                }
                if (href.includes('compradores.html') && tipo === 'CLIENTE') {
                    link.style.display = 'none';
                }
            }
        });

        // 2. Controlar Botão "Cadastrar" na página
        const botoesCadastrar = document.querySelectorAll('button[onclick="abrirFormulario()"]');
        botoesCadastrar.forEach(botao => {
            let permitir = false;
            if (tipo === 'ADMIN' || tipo === 'EDICAO') {
                permitir = true;
            } else if (tipo === 'FORNECEDOR' && configuracao.entidade === 'representantes') {
                permitir = true;
            }

            if (!permitir) {
                botao.style.display = 'none';
            } else {
                botao.style.display = 'inline-flex';
            }
        });
    } catch (e) {
        console.error('Erro ao aplicar controles de acesso:', e);
    }
}

async function carregarMenuLateral() {
    try {
        const resposta = await fetch('/layout/menu-lateral.html');
        if (resposta.ok) {
            const html = await resposta.text();
            const aside = document.getElementById('menu-lateral');
            if (aside) {
                aside.outerHTML = html;
            }
        }
    } catch (e) {
        console.error('Erro ao carregar menu lateral universal:', e);
    }

    const caminhoAtual = window.location.pathname;
    const mapaAtivo = {
        '/pages/fornecedores.html': 'menu-fornecedores',
        '/pages/compradores.html': 'menu-compradores',
        '/pages/representantes.html': 'menu-representantes',
        '/pages/clientes.html': 'menu-clientes',
        '/pages/produtos.html': 'menu-produtos',
        '/pages/admin.html': 'menu-admin'
    };

    const itemAtivoId = mapaAtivo[caminhoAtual];
    document.querySelectorAll('.menu-item').forEach(function(link) {
        const isActive = link.id === itemAtivoId;
        link.classList.toggle('active', isActive);
        if (isActive) {
            link.setAttribute('aria-current', 'page');
        } else {
            link.removeAttribute('aria-current');
        }
    });
}

document.addEventListener('DOMContentLoaded', async () => {
    console.log('DOMContentLoaded disparado');

    // Páginas com data-standalone gerenciam o próprio ciclo de vida (ex: admin.html)
    if (document.body.dataset.standalone === 'true') return;

    await carregarMenuLateral();
    setupCidadeAutocomplete();
    console.log('container user-info:', document.getElementById('user-info'));
    // Só executa o fluxo genérico nas páginas que possuem a tabela padrão (id="linhas")
    if (document.getElementById('linhas')) {
        carregar();
    }
    exibirInfoUsuario();
    aplicarControlesDeAcesso();
    
    // Carregar categorias e atividades para selects (usando funções do preenchimento-automatico.js)
    if (typeof carregarCategorias === 'function') {
        carregarCategorias('categoria');
    }
    if (typeof carregarAtividades === 'function') {
        carregarAtividades('atividade');
    }
    
    // Bind CNPJ blur listener dynamically
    const cnpjEl = document.getElementById('cnpj');
    if (cnpjEl) {
        cnpjEl.addEventListener('blur', consultarCnpj);
    }
    
    // Bind CNPJ Fornecedor listener dynamically
    const cnpjFornecedorEl = document.getElementById('cnpjFornecedor');
    if (cnpjFornecedorEl) {
        cnpjFornecedorEl.addEventListener('blur', buscarFornecedorPorCnpj);
        cnpjFornecedorEl.addEventListener('input', () => {
            const cnpj = cnpjFornecedorEl.value.replace(/\D/g, '');
            if (cnpj.length === 14) {
                buscarFornecedorPorCnpj();
            }
        });
    }
});

async function carregar() {
    try {
        const resposta = await fetch(endpoint);
        if (!resposta.ok) throw new Error('Não foi possível carregar os dados.');
        registros = await resposta.json();
        filtrar();
        atualizarIndicadores();
    } catch (erro) { mostrarAlerta(erro.message, true); }
}

function normalizarTexto(valor) {
    return String(valor || '').trim().toLowerCase();
}

function listarCidadesDisponiveis() {
    if (!Array.isArray(registros)) return [];
    const cidades = new Map();

    registros.forEach(item => {
        const cidade = String(item.cidade || '').trim();
        if (!cidade) return;
        cidades.set(cidade.toLowerCase(), cidade);
    });

    return Array.from(cidades.values()).sort((a, b) => a.localeCompare(b, 'pt-BR'));
}

function listarUfsDisponiveis() {
    if (!Array.isArray(registros)) return [];
    const ufs = new Set();

    registros.forEach(item => {
        const uf = String(item.estado || '').trim();
        if (!uf) return;
        ufs.add(uf.toUpperCase());
    });

    return Array.from(ufs).sort((a, b) => a.localeCompare(b, 'pt-BR'));
}

function atualizarSugestoesCidade() {
    const input = document.getElementById('filtro-cidade');
    const dropdown = document.getElementById('cidade-suggestions');
    if (!input || !dropdown) return;

    const termo = normalizarTexto(input.value);
    const cidades = listarCidadesDisponiveis();
    const filtradas = termo
        ? cidades.filter(cidade => normalizarTexto(cidade).includes(termo)).slice(0, 8)
        : cidades.slice(0, 8);

    if (!filtradas.length) {
        dropdown.innerHTML = '<div class="px-3 py-2 text-xs text-slate-400">Nenhuma cidade encontrada</div>';
        dropdown.classList.remove('hidden');
        return;
    }

    dropdown.innerHTML = filtradas.map(cidade => `
        <button type="button" class="sugestao-cidade" data-cidade="${cidade}">${cidade}</button>
    `).join('');

    dropdown.querySelectorAll('.sugestao-cidade').forEach(botao => {
        botao.addEventListener('mousedown', (event) => {
            event.preventDefault();
            input.value = botao.dataset.cidade;
            dropdown.classList.add('hidden');
            filtrar();
        });
    });

    dropdown.classList.remove('hidden');
}

function atualizarSugestoesUf() {
    const input = document.getElementById('filtro-uf');
    const dropdown = document.getElementById('uf-suggestions');
    if (!input || !dropdown) return;

    const termo = normalizarTexto(input.value);
    const ufs = listarUfsDisponiveis();
    const filtradas = termo
        ? ufs.filter(uf => normalizarTexto(uf).includes(termo)).slice(0, 8)
        : ufs.slice(0, 8);

    if (!filtradas.length) {
        dropdown.innerHTML = '<div class="px-3 py-2 text-xs text-slate-400">Nenhuma UF encontrada</div>';
        dropdown.classList.remove('hidden');
        return;
    }

    dropdown.innerHTML = filtradas.map(uf => `
        <button type="button" class="sugestao-cidade" data-uf="${uf}">${uf}</button>
    `).join('');

    dropdown.querySelectorAll('.sugestao-cidade').forEach(botao => {
        botao.addEventListener('mousedown', (event) => {
            event.preventDefault();
            input.value = botao.dataset.uf;
            dropdown.classList.add('hidden');
            filtrar();
        });
    });

    dropdown.classList.remove('hidden');
}

function setupCidadeAutocomplete() {
    const inputCidade = document.getElementById('filtro-cidade');
    const dropdownCidade = document.getElementById('cidade-suggestions');
    const inputUf = document.getElementById('filtro-uf');
    const dropdownUf = document.getElementById('uf-suggestions');

    if (inputCidade && dropdownCidade) {
        inputCidade.addEventListener('click', () => atualizarSugestoesCidade());
        inputCidade.addEventListener('focus', () => atualizarSugestoesCidade());
        inputCidade.addEventListener('input', () => {
            atualizarSugestoesCidade();
            filtrar();
        });

        inputCidade.addEventListener('keydown', (event) => {
            if (event.key === 'Escape') {
                dropdownCidade.classList.add('hidden');
            }
        });
    }

    if (inputUf && dropdownUf) {
        inputUf.addEventListener('click', () => atualizarSugestoesUf());
        inputUf.addEventListener('focus', () => atualizarSugestoesUf());
        inputUf.addEventListener('input', () => {
            atualizarSugestoesUf();
            filtrar();
        });

        inputUf.addEventListener('keydown', (event) => {
            if (event.key === 'Escape') {
                dropdownUf.classList.add('hidden');
            }
        });
    }

    document.addEventListener('click', (event) => {
        const target = event.target;
        if (inputCidade && !inputCidade.contains(target) && dropdownCidade && !dropdownCidade.contains(target)) {
            dropdownCidade.classList.add('hidden');
        }
        if (inputUf && !inputUf.contains(target) && dropdownUf && !dropdownUf.contains(target)) {
            dropdownUf.classList.add('hidden');
        }
    });
}

function filtrar() {
const busca = document.getElementById('busca');
    const termo = busca ? normalizarTexto(busca.value) : '';
    const cidadeFiltro = document.getElementById('filtro-cidade') ? normalizarTexto(document.getElementById('filtro-cidade').value) : '';
    const ufFiltro = document.getElementById('filtro-uf') ? normalizarTexto(document.getElementById('filtro-uf').value) : '';

    let exibidos = registros.filter(item => {
        const textoGeral = [item.nome, item.cnpj, item.status, item.cidade, item.estado, item.contato, item.email]
            .some(valor => normalizarTexto(valor).includes(termo));

        const atendeCidade = !cidadeFiltro || normalizarTexto(item.cidade).includes(cidadeFiltro);
        const atendeUf = !ufFiltro || normalizarTexto(item.estado).includes(ufFiltro);

        return textoGeral && atendeCidade && atendeUf;
    });
    const linhas = document.getElementById('linhas');
    if (!linhas) return;
    linhas.innerHTML = exibidos.length ? exibidos.map(linha).join('') : '<tr><td colspan="7" class="p-10 text-center text-slate-500">Nenhum cadastro encontrado.</td></tr>';
}

function linha(item) {
    // Obter tipo de usuário logado para verificar permissões de edição
    let tipo = '';
    try {
        const usuarioJson = localStorage.getItem('usuario');
        if (usuarioJson) {
            tipo = JSON.parse(usuarioJson).tipo;
        }
    } catch (e) {
        console.error('Erro ao verificar tipo de usuário logado:', e);
    }

    // Regras de botões de ação na tabela conforme o tipo de usuário e página atual
    let botoesAcao = '';
    const isRepresentantesPage = configuracao.entidade === 'representantes';
    const isFornecedoresPage = configuracao.entidade === 'fornecedores';
    const isCompradoresPage = configuracao.entidade === 'compradores';
    const isClientesPage = configuracao.entidade === 'clientes';

    if (tipo === 'ADMIN') {
        botoesAcao = `
            <button onclick="editar(${item.id})" class="mr-2 text-sky-700 hover:text-sky-900 transition" title="Editar"><i class="fa-solid fa-pen"></i></button>
            <button onclick="excluirRegistro(${item.id})" class="text-rose-700 hover:text-rose-900 transition" title="Inativar"><i class="fa-solid fa-ban"></i></button>
        `;
    } else if (tipo === 'EDICAO' || tipo === 'MANUTENCAO') {
        botoesAcao = `
            <button onclick="editar(${item.id})" class="mr-2 text-sky-700 hover:text-sky-900 transition" title="Editar"><i class="fa-solid fa-pen"></i></button>
        `;
    } else if (tipo === 'FORNECEDOR') {
        if (isRepresentantesPage) {
            botoesAcao = `
                <button onclick="editar(${item.id})" class="mr-2 text-sky-700 hover:text-sky-900 transition" title="Editar"><i class="fa-solid fa-pen"></i></button>
                <button onclick="excluirRegistro(${item.id})" class="text-rose-700 hover:text-rose-900 transition" title="Inativar"><i class="fa-solid fa-ban"></i></button>
            `;
        } else if (isFornecedoresPage) {
            botoesAcao = `
                <button onclick="editar(${item.id})" class="mr-2 text-sky-700 hover:text-sky-900 transition" title="Editar"><i class="fa-solid fa-pen"></i></button>
            `;
        } else {
            botoesAcao = `<span class="text-xs text-slate-400 italic">Apenas visualização</span>`;
        }
    } else if (tipo === 'COMPRADOR' && isCompradoresPage) {
        botoesAcao = `
            <button onclick="editar(${item.id})" class="mr-2 text-sky-700 hover:text-sky-900 transition" title="Editar"><i class="fa-solid fa-pen"></i></button>
        `;
    } else if (tipo === 'CLIENTE' && isClientesPage) {
        botoesAcao = `
            <button onclick="editar(${item.id})" class="mr-2 text-sky-700 hover:text-sky-900 transition" title="Editar"><i class="fa-solid fa-pen"></i></button>
        `;
    } else if (tipo === 'REPRESENTANTE' && isRepresentantesPage) {
        botoesAcao = `
            <button onclick="editar(${item.id})" class="mr-2 text-sky-700 hover:text-sky-900 transition" title="Editar"><i class="fa-solid fa-pen"></i></button>
        `;
    } else {
        botoesAcao = `<span class="text-xs text-slate-400 italic">Apenas visualização</span>`;
    }

    if (configuracao.entidade === 'representantes') {
        return `<tr class="hover:bg-slate-50">
            <td class="p-4 font-medium">${item.id}</td>
            <td class="p-4 font-semibold">${escapar(item.nome)}</td>
            <td class="p-4">${escapar(item.contato || 'N/A')}</td>
            <td class="p-4">${escapar(item.email || 'N/A')}</td>
            <td class="p-4"><span class="rounded-full bg-slate-100 px-2 py-1 text-xs font-bold">${formatarStatus(item.status)}</span></td>
            <td class="p-4 text-center">${botoesAcao}</td>
        </tr>`;
    }

    if (configuracao.entidade === 'clientes') {
        const docFormatado = item.cpfCnpj ? item.cpfCnpj.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4') : 'Não informado';
        return `<tr class="hover:bg-slate-50">
            <td class="p-4 font-medium">${item.id}</td>
            <td class="p-4 font-semibold">${escapar(item.nome)}</td>
            <td class="p-4 font-mono">${docFormatado}</td>
            <td class="p-4"><span class="rounded-full bg-slate-100 px-2 py-1 text-xs font-bold">${formatarStatus(item.status)}</span></td>
            <td class="p-4 text-center">${botoesAcao}</td>
        </tr>`;
    }

    const endereco = [item.logradouro, item.numero].filter(Boolean).join(', ');
    const cidade = [item.cidade, item.estado].filter(Boolean).join('/');

    return `<tr class="hover:bg-slate-50">
        <td class="p-4 font-medium">${item.id}</td>
        <td class="p-4 font-semibold">${escapar(item.nome)}</td>
        <td class="p-4">${formatarCnpj(item.cnpj)}</td>
        <td class="p-4"><span class="rounded-full bg-slate-100 px-2 py-1 text-xs font-bold">${formatarStatus(item.status)}</span></td>
        <td class="p-4">${Number(item.pontuacaoRisco || 0).toLocaleString('pt-BR', {minimumFractionDigits: 1})}</td>
        <td class="p-4 text-slate-600">${escapar(endereco || 'Não informado')}<br><span class="text-xs">${escapar(cidade)}</span></td>
        <td class="p-4 text-center">${botoesAcao}</td>
    </tr>`;
}

function atualizarIndicadores() {
    const total = registros.length;
    const media = total ? registros.reduce((s, i) => s + Number(i.pontuacaoRisco || 0), 0) / total : 0;
    
    const totalEl = document.getElementById('total');
    if (totalEl) totalEl.textContent = total;
    
    const analiseEl = document.getElementById('analise');
    if (analiseEl) analiseEl.textContent = registros.filter(i => i.status === 'EM_ANALISE').length;
    
    const mediaEl = document.getElementById('media');
    if (mediaEl) mediaEl.textContent = media.toLocaleString('pt-BR', {minimumFractionDigits: 1, maximumFractionDigits: 1});
    
    const criticosEl = document.getElementById('criticos');
    if (criticosEl) criticosEl.textContent = registros.filter(i => Number(i.pontuacaoRisco || 0) >= 7).length;
}

function abrirFormulario() {
    ultimoCnpjBuscado = '';
    document.getElementById('formulario').reset(); document.getElementById('id').value = '';
    const codEmpresaLabel = document.getElementById('codEmpresaLabel');
    if (codEmpresaLabel) {
        codEmpresaLabel.textContent = 'Automático';
    }
    document.getElementById('titulo-form').textContent = `Cadastrar ${configuracao.singular.toLowerCase()}`;
    document.getElementById('modal').classList.remove('hidden');
}
function fecharFormulario() { document.getElementById('modal').classList.add('hidden'); }
function editar(id) {
    const item = registros.find(i => i.id === id); if (!item) return;
    
    ['id','codCidade','nome','status','cep','logradouro','numero','complemento','bairro','cidade','estado','cnpj','cnpjFornecedor','codEmpresa','cpfCnpj','tipoPessoa','latitude','longitude','contato','email','prazoEntregaDias'].forEach(campo => {
        const el = document.getElementById(campo);
        if (el) el.value = item[campo] || '';
    });

    const codEmpresaEl = document.getElementById('codEmpresa');
    const codEmpresaLabel = document.getElementById('codEmpresaLabel');
    if (codEmpresaLabel) {
        codEmpresaLabel.textContent = (codEmpresaEl && codEmpresaEl.value) ? codEmpresaEl.value : 'Automático';
    }
    
    const pontuacao = document.getElementById('pontuacao');
    if (pontuacao) pontuacao.value = item.pontuacaoRisco || 0;
    
    const cnpjEl = document.getElementById('cnpj');
    if (cnpjEl && item.cnpj) cnpjEl.value = formatarCnpj(item.cnpj);
    
    const cnpjFornecedorEl = document.getElementById('cnpjFornecedor');
    if (cnpjFornecedorEl && item.cnpjFornecedor) {
        cnpjFornecedorEl.value = formatarCnpj(item.cnpjFornecedor);
        ultimoCnpjBuscado = item.cnpjFornecedor.replace(/\D/g, '');
    } else {
        ultimoCnpjBuscado = '';
    }
    
    // Preencher categoria e atividade (se existem)
    const categoriaEl = document.getElementById('categoria');
    if (categoriaEl && item.categoria) {
        categoriaEl.value = item.categoria.id || '';
    }
    
    const atividadeEl = document.getElementById('atividade');
    if (atividadeEl && item.atividade) {
        atividadeEl.value = item.atividade.id || '';
    }
    
    document.getElementById('titulo-form').textContent = `Editar ${configuracao.singular.toLowerCase()}`;
    document.getElementById('modal').classList.remove('hidden');
}
async function salvar(evento) {
    evento.preventDefault();
    limparErros();

    let dados = {};
    let temErros = false;

    if (configuracao.entidade === 'representantes') {
        const nome = document.getElementById('nome');
        const cnpj = document.getElementById('cnpj');
        const cnpjFornecedor = document.getElementById('cnpjFornecedor');

        if (!nome.value.trim()) {
            mostrarErro(nome, 'O nome do representante é obrigatório.');
            temErros = true;
        }
        if (cnpj.value.replace(/\D/g, '').length !== 14) {
            mostrarErro(cnpj, 'O CNPJ do representante deve ter 14 dígitos.');
            temErros = true;
        }
        if (cnpjFornecedor.value.replace(/\D/g, '').length !== 14) {
            mostrarErro(cnpjFornecedor, 'O CNPJ do fornecedor deve ter 14 dígitos.');
            temErros = true;
        }

        if (temErros) {
            mostrarAlerta('Por favor, corrija os erros no formulário.', true);
            return;
        }

        dados = {
            nome: nome.value.trim(),
            status: document.getElementById('status').value,
            contato: document.getElementById('contato')?.value.trim() || '',
            email: document.getElementById('email')?.value.trim() || '',
            cnpj: cnpj.value.replace(/\D/g, ''),
            cnpjFornecedor: cnpjFornecedor.value.replace(/\D/g, ''),
            codEmpresa: document.getElementById('codEmpresa').value ? Number(document.getElementById('codEmpresa').value) : null,
            cep: document.getElementById('cep')?.value.trim() || null,
            logradouro: document.getElementById('logradouro')?.value.trim() || null,
            numero: document.getElementById('numero')?.value.trim() || null,
            complemento: document.getElementById('complemento')?.value.trim() || null,
            bairro: document.getElementById('bairro')?.value.trim() || null,
            cidade: document.getElementById('cidade')?.value.trim() || null,
            estado: document.getElementById('estado')?.value.trim() || null,
            latitude: document.getElementById('latitude')?.value ? Number(document.getElementById('latitude').value) : null,
            longitude: document.getElementById('longitude')?.value ? Number(document.getElementById('longitude').value) : null
        };
        
        // Adicionar categoria e atividade se selecionadas (OPCIONAIS)
        const categoriaRepEl = document.getElementById('categoria');
        const atividadeRepEl = document.getElementById('atividade');
        if (categoriaRepEl && categoriaRepEl.value && categoriaRepEl.value !== '') {
            dados.categoria = { id: Number(categoriaRepEl.value) };
        }
        if (atividadeRepEl && atividadeRepEl.value && atividadeRepEl.value !== '') {
            dados.atividade = { id: Number(atividadeRepEl.value) };
        }
    } else if (configuracao.entidade === 'clientes') {
        const nome = document.getElementById('nome');
        const cpfCnpj = document.getElementById('cpfCnpj');

        if (!nome.value.trim()) {
            mostrarErro(nome, 'O nome do cliente é obrigatório.');
            temErros = true;
        }
        if (cpfCnpj.value.replace(/\D/g, '').length !== 11) {
            mostrarErro(cpfCnpj, 'O CPF deve ter 11 dígitos.');
            temErros = true;
        }

        if (temErros) {
            mostrarAlerta('Por favor, corrija os erros no formulário.', true);
            return;
        }

        dados = {
            nome: nome.value.trim(),
            status: document.getElementById('status').value,
            cpfCnpj: cpfCnpj.value.replace(/\D/g, ''),
            tipoPessoa: 'PF', // Clientes são sempre PF neste sistema
            cep: document.getElementById('cep')?.value.trim() || null,
            logradouro: document.getElementById('logradouro')?.value.trim() || null,
            numero: document.getElementById('numero')?.value.trim() || null,
            complemento: document.getElementById('complemento')?.value.trim() || null,
            bairro: document.getElementById('bairro')?.value.trim() || null,
            cidade: document.getElementById('cidade')?.value.trim() || null,
            estado: document.getElementById('estado')?.value.trim() || null,
            latitude: document.getElementById('latitude')?.value ? Number(document.getElementById('latitude').value) : null,
            longitude: document.getElementById('longitude')?.value ? Number(document.getElementById('longitude').value) : null
        };
    } else { // Fornecedores e Compradores
        const nome = document.getElementById('nome');
        const cnpj = document.getElementById('cnpj');

        if (!nome.value.trim()) {
            mostrarErro(nome, 'O nome é obrigatório.');
            temErros = true;
        }
        if (cnpj && cnpj.value.replace(/\D/g, '').length !== 14) {
            mostrarErro(cnpj, 'O CNPJ deve ter 14 dígitos.');
            temErros = true;
        }

        if (temErros) {
            mostrarAlerta('Por favor, corrija os erros no formulário.', true);
            return;
        }

        dados = Object.fromEntries(['nome','status','cep','logradouro','numero','complemento','bairro','cidade','estado','latitude','longitude'].map(campo => [campo, document.getElementById(campo)?.value.trim()]));
        dados.codCidade = document.getElementById('codCidade')?.value || null;
        if(cnpj) dados.cnpj = cnpj.value.replace(/\D/g, '');
        const pontuacao = document.getElementById('pontuacao');
        if(pontuacao) dados.pontuacaoRisco = Number(pontuacao.value || 0);
        const prazoEntrega = document.getElementById('prazoEntregaDias');
        if (prazoEntrega) dados.prazoEntregaDias = Number(prazoEntrega.value || 0);
        
        // Adicionar categoria e atividade se existirem e forem selecionadas (OPCIONAIS)
        const categoriaEl = document.getElementById('categoria');
        const atividadeEl = document.getElementById('atividade');
        if (categoriaEl && categoriaEl.value && categoriaEl.value !== '') {
            dados.categoria = { id: Number(categoriaEl.value) };
        }
        if (atividadeEl && atividadeEl.value && atividadeEl.value !== '') {
            dados.atividade = { id: Number(atividadeEl.value) };
        }
    }

    const id = document.getElementById('id').value;

    try {
        console.log('[DEBUG] Dados a serem salvos:', JSON.stringify(dados, null, 2));
        console.log('[DEBUG] ID:', id);
        console.log('[DEBUG] Endpoint:', id ? `${endpoint}/${id}` : endpoint);
        console.log('[DEBUG] Método:', id ? 'PUT' : 'POST');
        
        const resposta = await fetch(id ? `${endpoint}/${id}` : endpoint, {
            method: id ? 'PUT' : 'POST', 
            headers: {'Content-Type':'application/json'}, 
            body: JSON.stringify(dados)
        });
        
        console.log('[DEBUG] Status da resposta:', resposta.status);
        
        if (!resposta.ok) {
            const erroMsg = await resposta.text();
            console.error('[DEBUG] Erro na resposta:', resposta.status, erroMsg);
            throw new Error(erroMsg || 'Não foi possível salvar. Verifique se os dados são válidos.');
        }
        
        const resultado = await resposta.json();
        console.log('[DEBUG] Resultado do salvamento:', resultado);
        
        fecharFormulario();
        mostrarAlerta(`${configuracao.singular} salvo com sucesso.`);
        carregar();
    } catch (erro) {
        console.error('[DEBUG] Erro geral:', erro);
        mostrarAlerta(erro.message, true);
    }
}

function mostrarErro(elemento, mensagem) {
    elemento.classList.add('border-rose-500', 'ring-rose-500');
    const erroMsg = document.createElement('p');
    erroMsg.className = 'text-xs text-rose-600 mt-1 font-semibold erro-mensagem';
    erroMsg.textContent = mensagem;
    elemento.parentNode.appendChild(erroMsg);
}

function limparErros() {
    document.querySelectorAll('.erro-mensagem').forEach(e => e.remove());
    document.querySelectorAll('.border-rose-500').forEach(el => {
        el.classList.remove('border-rose-500', 'ring-rose-500');
    });
}
async function excluirRegistro(id) {
    if (!confirm(`Inativar este ${configuracao.singular.toLowerCase()}? O registro será marcado como INATIVO.`)) return;
    try { const resposta = await fetch(`${endpoint}/${id}`, {method:'DELETE'}); if (!resposta.ok) throw new Error('Não foi possível inativar o cadastro.'); mostrarAlerta('Cadastro inativado com sucesso.'); carregar(); } catch (erro) { mostrarAlerta(erro.message, true); }
}
async function consultarCep() {
    const cep = document.getElementById('cep').value.replace(/\D/g, ''); 
    if (cep.length !== 8) {
        console.warn('CEP deve ter 8 dígitos');
        return;
    }
    
    try { 
        console.log('Consultando CEP:', cep);
        const respostaViaCep = await fetch(`https://viacep.com.br/ws/${cep}/json/`); 
        const dadosViaCep = await respostaViaCep.json(); 
        
        if (dadosViaCep.erro) {
            throw new Error('CEP não encontrado.');
        }
        
        console.log('Dados do ViaCEP:', dadosViaCep);
        
        document.getElementById('cep').value = `${cep.slice(0,5)}-${cep.slice(5)}`;
        document.getElementById('logradouro').value = dadosViaCep.logradouro || '';
        document.getElementById('bairro').value = dadosViaCep.bairro || '';
        document.getElementById('cidade').value = dadosViaCep.localidade || '';
        document.getElementById('estado').value = dadosViaCep.uf || '';
        
        // Buscar geolocalização usando Nominatim (API gratuita do OpenStreetMap)
        const enderecoCompleto = `${dadosViaCep.logradouro}, ${dadosViaCep.localidade}, ${dadosViaCep.uf}, Brasil`;
        try {
            console.log('Consultando geolocalização:', enderecoCompleto);
            const respostaGeo = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(enderecoCompleto)}`);
            const dadosGeo = await respostaGeo.json();
            
            if (dadosGeo && dadosGeo.length > 0) {
                const latitude = parseFloat(dadosGeo[0].lat);
                const longitude = parseFloat(dadosGeo[0].lon);
                
                document.getElementById('latitude').value = latitude;
                document.getElementById('longitude').value = longitude;
                console.log('Geolocalização salva:', latitude, longitude);
            }
        } catch (geoError) {
            console.warn('Não foi possível obter geolocalização:', geoError);
            // O CEP ainda pode ser salvo sem geolocalização
        }
        
        // Buscar código da cidade no banco
        const cidade = await fetch(`/api/cidades/ibge/${dadosViaCep.ibge}`);
        if (cidade.ok) {
            const cidadeData = await cidade.json();
            document.getElementById('codCidade').value = cidadeData.codCidade || '';
        }
        
    } catch (erro) { 
        console.error('Erro ao consultar CEP:', erro);
        mostrarAlerta('Não foi possível consultar o CEP.', true); 
    }
}

async function consultarCnpj() {
    const cnpjEl = document.getElementById('cnpj');
    if (!cnpjEl) return;
    
    const cnpj = cnpjEl.value.replace(/\D/g, '');
    if (cnpj.length !== 14) {
        return;
    }
    
    try {
        console.log('Consultando CNPJ na API pública:', cnpj);
        mostrarAlerta('Consultando informações do CNPJ...', false);
        
        const resposta = await fetch(`https://brasilapi.com.br/api/cnpj/v1/${cnpj}`);
        if (!resposta.ok) {
            throw new Error('CNPJ não encontrado ou erro na API pública.');
        }
        
        const dados = await resposta.json();
        console.log('Dados do CNPJ obtidos:', dados);
        
        // Preencher Nome (Razão Social ou Nome Fantasia)
        const nomeEl = document.getElementById('nome');
        if (nomeEl && !nomeEl.value.trim()) {
            nomeEl.value = dados.razao_social || dados.nome_fantasia || '';
        }
        
        // Preencher CEP
        const cepEl = document.getElementById('cep');
        if (cepEl) {
            cepEl.value = dados.cep || '';
            // Disparar consulta de CEP para obter latitude, longitude e codCidade!
            if (dados.cep) {
                await consultarCep();
            }
        }
        
        // Fallback para preencher demais campos se o CEP falhar ou vier vazio
        const logradouroEl = document.getElementById('logradouro');
        if (logradouroEl && !logradouroEl.value.trim() && dados.logradouro) {
            logradouroEl.value = dados.logradouro;
        }
        const numeroEl = document.getElementById('numero');
        if (numeroEl && !numeroEl.value.trim() && dados.numero) {
            numeroEl.value = dados.numero;
        }
        const complementoEl = document.getElementById('complemento');
        if (complementoEl && !complementoEl.value.trim() && dados.complemento) {
            complementoEl.value = dados.complemento;
        }
        const bairroEl = document.getElementById('bairro');
        if (bairroEl && !bairroEl.value.trim() && dados.bairro) {
            bairroEl.value = dados.bairro;
        }
        const cidadeEl = document.getElementById('cidade');
        if (cidadeEl && !cidadeEl.value.trim() && dados.municipio) {
            cidadeEl.value = dados.municipio;
        }
        const estadoEl = document.getElementById('estado');
        if (estadoEl && !estadoEl.value.trim() && dados.uf) {
            estadoEl.value = dados.uf;
        }
        
        mostrarAlerta('Informações do CNPJ preenchidas automaticamente.');
        
        // Tenta preencher atividade automaticamente via CNAE se a função estiver disponível
        if (typeof preencherAtividadeAutomatica === 'function' && (dados.cnae || dados.atividade)) {
            preencherAtividadeAutomatica(dados.cnae, dados.atividade);
        }
        
    } catch (erro) {
        console.error('Erro ao consultar CNPJ:', erro);
        mostrarAlerta('Não foi possível obter dados do CNPJ da API pública. Preencha os campos manualmente.', true);
    }
}

// Função específica para comprador/representante consultar CNPJ
async function consultarCnpjComprador() {
    const cnpjEl = document.getElementById('cnpj');
    const nomeEl = document.getElementById('nome');
    
    if (!cnpjEl) return;
    
    const cnpj = cnpjEl.value.replace(/\D/g, '');
    if (cnpj.length !== 14) return;
    
    try {
        console.log('Consultando CNPJ comprador/representante:', cnpj);
        
        // Tentar API interna primeiro
        let dados = null;
        try {
            const respostaInterna = await fetch(`/api/consulta-cnpj/${cnpj}`);
            if (respostaInterna.ok) {
                dados = await respostaInterna.json();
            }
        } catch (e) {
            console.warn('Falha na consulta interna, tentando API pública');
        }
        
        // Se não conseguiu internamente, tentar API pública
        if (!dados) {
            try {
                const respostaBrasilApi = await fetch(`https://brasilapi.com.br/api/cnpj/v1/${cnpj}`);
                if (respostaBrasilApi.ok) {
                    const dadosBrasilApi = await respostaBrasilApi.json();
                    dados = {
                        razao_social: dadosBrasilApi.razao_social,
                        nome_fantasia: dadosBrasilApi.nome_fantasia,
                        cep: dadosBrasilApi.cep,
                        logradouro: dadosBrasilApi.logradouro,
                        numero: dadosBrasilApi.numero,
                        complemento: dadosBrasilApi.complemento,
                        bairro: dadosBrasilApi.bairro,
                        municipio: dadosBrasilApi.municipio,
                        uf: dadosBrasilApi.uf
                    };
                }
            } catch (e) {
                console.warn('Falha na API pública:', e);
            }
        }
        
        if (!dados) {
            mostrarAlerta('CNPJ não encontrado em nenhuma base de dados.', true);
            return;
        }
        
        console.log('Dados obtidos:', dados);
        
        // Preencher Nome
        if (nomeEl && !nomeEl.value.trim()) {
            nomeEl.value = dados.razao_social || dados.nome_fantasia || '';
        }
        
        // Preencher CEP
        const cepEl = document.getElementById('cep');
        if (cepEl && dados.cep) {
            cepEl.value = dados.cep;
            mascaraCep(cepEl);
            // Disparar consulta de CEP
            await consultarCep();
        }
        
        // Preencher campos de endereço
        const logradouroEl = document.getElementById('logradouro');
        if (logradouroEl && !logradouroEl.value.trim() && dados.logradouro) {
            logradouroEl.value = dados.logradouro;
        }
        
        const numeroEl = document.getElementById('numero');
        if (numeroEl && !numeroEl.value.trim() && dados.numero) {
            numeroEl.value = dados.numero;
        }
        
        const complementoEl = document.getElementById('complemento');
        if (complementoEl && !complementoEl.value.trim() && dados.complemento) {
            complementoEl.value = dados.complemento;
        }
        
        const bairroEl = document.getElementById('bairro');
        if (bairroEl && !bairroEl.value.trim() && dados.bairro) {
            bairroEl.value = dados.bairro;
        }
        
        const cidadeEl = document.getElementById('cidade');
        if (cidadeEl && !cidadeEl.value.trim() && (dados.municipio || dados.localidade)) {
            cidadeEl.value = dados.municipio || dados.localidade || '';
        }
        
        const estadoEl = document.getElementById('estado');
        if (estadoEl && !estadoEl.value.trim() && dados.uf) {
            estadoEl.value = dados.uf;
        }
        
        mostrarAlerta('Informações do CNPJ preenchidas automaticamente.');
        
        // Tenta preencher atividade automaticamente via CNAE se a função estiver disponível
        if (typeof preencherAtividadeAutomatica === 'function' && (dados.cnae || dados.atividade)) {
            preencherAtividadeAutomatica(dados.cnae, dados.atividade);
        }
        
    } catch (erro) {
        console.error('Erro ao consultar CNPJ:', erro);
        mostrarAlerta('Erro ao consultar CNPJ. Preencha os dados manualmente.', true);
    }
}

function mascaraCep(input) {
    let valor = input.value.replace(/\D/g, '').slice(0, 8);
    input.value = valor.replace(/^(\d{5})(\d)/, '$1-$2');
}
function mascaraCnpj(input) { let valor = input.value.replace(/\D/g, '').slice(0,14); input.value = valor.replace(/^(\d{2})(\d)/,'$1.$2').replace(/^(\d{2}\.\d{3})(\d)/,'$1.$2').replace(/(\d{3}\.)(\d{3})(\d)/,'$1$2/$3').replace(/(\d{4}\/)(\d{2})$/,'$1$2'); }
function formatarCnpj(valor) { const cnpj = String(valor || '').replace(/\D/g,''); return cnpj.length === 14 ? cnpj.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5') : valor || ''; }
function formatarStatus(status) { return ({EM_ANALISE:'Em análise', APROVADO:'Aprovado', REJEITADO:'Rejeitado', SUSPENSO:'Suspenso'})[status] || status || 'Em análise'; }
function escapar(valor) { const elemento = document.createElement('span'); elemento.textContent = valor || ''; return elemento.innerHTML; }
function mostrarAlerta(mensagem, erro = false) { const alerta = document.getElementById('alerta'); if (!alerta) return; alerta.textContent = mensagem; alerta.className = `mb-5 rounded-lg border px-4 py-3 text-sm ${erro ? 'border-rose-200 bg-rose-50 text-rose-800' : 'border-emerald-200 bg-emerald-50 text-emerald-800'}`; alerta.classList.remove('hidden'); }

async function buscarFornecedorPorCnpj() {
    const cnpjFornecedorEl = document.getElementById('cnpjFornecedor');
    if (!cnpjFornecedorEl) return;

    const cnpj = cnpjFornecedorEl.value.replace(/\D/g, '');
    const codEmpresaLabel = document.getElementById('codEmpresaLabel');
    if (cnpj.length !== 14) {
        const codEmpresaEl = document.getElementById('codEmpresa');
        if (codEmpresaEl) codEmpresaEl.value = '';
        if (codEmpresaLabel) codEmpresaLabel.textContent = 'Automático';
        ultimoCnpjBuscado = '';
        return;
    }

    if (cnpj === ultimoCnpjBuscado) {
        return;
    }

    try {
        ultimoCnpjBuscado = cnpj;
        console.log('Consultando fornecedor por CNPJ no banco de dados:', cnpj);
        mostrarAlerta('Buscando fornecedor cadastrado pelo CNPJ...', false);
        
        const resposta = await fetch(`/api/fornecedores/cnpj/${cnpj}`);
        if (resposta.ok) {
            const fornecedor = await resposta.json();
            const codEmpresaEl = document.getElementById('codEmpresa');
            if (codEmpresaEl) {
                codEmpresaEl.value = fornecedor.id;
            }
            if (codEmpresaLabel) {
                codEmpresaLabel.textContent = fornecedor.id;
            }
            mostrarAlerta(`Fornecedor vinculado com sucesso: ${fornecedor.nome}`);
        } else {
            const codEmpresaEl = document.getElementById('codEmpresa');
            if (codEmpresaEl) {
                codEmpresaEl.value = '';
            }
            if (codEmpresaLabel) {
                codEmpresaLabel.textContent = 'Automático';
            }
            alert('O CNPJ do fornecedor informado não existe. É necessário que o mesmo seja cadastrado pela empresa fornecedora.');
            mostrarAlerta('O CNPJ do fornecedor informado não existe. É necessário que o mesmo seja cadastrado pela empresa fornecedora.', true);
        }
    } catch (erro) {
        console.error('Erro ao buscar fornecedor pelo CNPJ:', erro);
        mostrarAlerta('Erro ao verificar o CNPJ do fornecedor.', true);
    }
}

// Função específica para representante consultar CNPJ
async function consultarCnpjRepresentante() {
    const cnpjEl = document.getElementById('cnpj');
    const nomeEl = document.getElementById('nome');
    
    if (!cnpjEl) return;
    
    const cnpj = cnpjEl.value.replace(/\D/g, '');
    if (cnpj.length !== 14) return;
    
    try {
        console.log('Consultando CNPJ representante:', cnpj);
        
        // Tentar API interna primeiro
        let dados = null;
        try {
            const respostaInterna = await fetch(`/api/consulta-cnpj/${cnpj}`);
            if (respostaInterna.ok) {
                dados = await respostaInterna.json();
            }
        } catch (e) {
            console.warn('Falha na consulta interna, tentando API pública');
        }
        
        // Se não conseguiu internamente, tentar API pública
        if (!dados) {
            try {
                const respostaBrasilApi = await fetch(`https://brasilapi.com.br/api/cnpj/v1/${cnpj}`);
                if (respostaBrasilApi.ok) {
                    const dadosBrasilApi = await respostaBrasilApi.json();
                    dados = {
                        razao_social: dadosBrasilApi.razao_social,
                        nome_fantasia: dadosBrasilApi.nome_fantasia,
                        cep: dadosBrasilApi.cep,
                        logradouro: dadosBrasilApi.logradouro,
                        numero: dadosBrasilApi.numero,
                        complemento: dadosBrasilApi.complemento,
                        bairro: dadosBrasilApi.bairro,
                        municipio: dadosBrasilApi.municipio,
                        uf: dadosBrasilApi.uf
                    };
                }
            } catch (e) {
                console.warn('Falha na API pública:', e);
            }
        }
        
        if (!dados) {
            mostrarAlerta('CNPJ não encontrado em nenhuma base de dados.', true);
            return;
        }
        
        console.log('Dados obtidos:', dados);
        
        // Preencher Nome (será nome fantasia da empresa para o representante)
        if (nomeEl && !nomeEl.value.trim()) {
            nomeEl.value = dados.nome_fantasia || dados.razao_social || '';
        }
        
        // Preencher CEP
        const cepEl = document.getElementById('cep');
        if (cepEl && dados.cep) {
            cepEl.value = dados.cep;
            mascaraCep(cepEl);
            // Disparar consulta de CEP
            await consultarCep();
        }
        
        // Preencher campos de endereço
        const logradouroEl = document.getElementById('logradouro');
        if (logradouroEl && !logradouroEl.value.trim() && dados.logradouro) {
            logradouroEl.value = dados.logradouro;
        }
        
        const numeroEl = document.getElementById('numero');
        if (numeroEl && !numeroEl.value.trim() && dados.numero) {
            numeroEl.value = dados.numero;
        }
        
        const complementoEl = document.getElementById('complemento');
        if (complementoEl && !complementoEl.value.trim() && dados.complemento) {
            complementoEl.value = dados.complemento;
        }
        
        const bairroEl = document.getElementById('bairro');
        if (bairroEl && !bairroEl.value.trim() && dados.bairro) {
            bairroEl.value = dados.bairro;
        }
        
        const cidadeEl = document.getElementById('cidade');
        if (cidadeEl && !cidadeEl.value.trim() && (dados.municipio || dados.localidade)) {
            cidadeEl.value = dados.municipio || dados.localidade || '';
        }
        
        const estadoEl = document.getElementById('estado');
        if (estadoEl && !estadoEl.value.trim() && dados.uf) {
            estadoEl.value = dados.uf;
        }
        
        mostrarAlerta('Informações do CNPJ preenchidas automaticamente.');
        
        // Tenta preencher atividade automaticamente via CNAE se a função estiver disponível
        if (typeof preencherAtividadeAutomatica === 'function' && (dados.cnae || dados.atividade)) {
            preencherAtividadeAutomatica(dados.cnae, dados.atividade);
        }
        
    } catch (erro) {
        console.error('Erro ao consultar CNPJ:', erro);
        mostrarAlerta('Erro ao consultar CNPJ. Preencha os dados manualmente.', true);
    }
}

// Funções para carregar categorias e atividades nos formulários
async function carregarCategoriasNoFormulario() {
    const selectElement = document.getElementById('categoria');
    if (!selectElement) return;

    try {
        const response = await fetch('/api/categorias/ativas');
        const categorias = await response.json();

        // Limpa opções existentes
        selectElement.innerHTML = '<option value="">Selecione uma categoria...</option>';

        // Adiciona categorias
        categorias.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.nome || '';
            selectElement.appendChild(option);
        });

        // Adiciona opção "Adicionar nova categoria" com separador
        const separador = document.createElement('option');
        separador.disabled = true;
        separador.textContent = '───────────────';
        selectElement.appendChild(separador);

        const novaOption = document.createElement('option');
        novaOption.value = 'nova';
        novaOption.textContent = '➕ Adicionar nova categoria';
        selectElement.appendChild(novaOption);

        // Listener para abrir modal
        selectElement.addEventListener('change', function() {
            if (this.value === 'nova') {
                abrirModalAdicionarCategoria(selectElement);
                this.value = '';
            }
        });

    } catch (erro) {
        console.error('Erro ao carregar categorias:', erro);
    }
}

async function carregarAtividadesNoFormulario() {
    const selectElement = document.getElementById('atividade');
    if (!selectElement) return;

    try {
        const response = await fetch('/api/atividades/ativas');
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

        // Adiciona grupos
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

        // Adiciona opção "Adicionar nova atividade"
        const separador = document.createElement('option');
        separador.disabled = true;
        separador.textContent = '───────────────';
        selectElement.appendChild(separador);

        const novaOption = document.createElement('option');
        novaOption.value = 'nova';
        novaOption.textContent = '➕ Adicionar nova atividade';
        selectElement.appendChild(novaOption);

        // Listener para abrir modal
        selectElement.addEventListener('change', function() {
            if (this.value === 'nova') {
                abrirModalAdicionarAtividade(selectElement);
                this.value = '';
            }
        });

    } catch (erro) {
        console.error('Erro ao carregar atividades:', erro);
    }
}

async function abrirModalAdicionarCategoria(selectElement) {
    const nome = prompt('Digite o nome da nova categoria:');
    if (!nome || !nome.trim()) return;

    try {
        const response = await fetch('/api/categorias', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                nome: nome.trim(), 
                descricao: '', 
                icone: '', 
                ativa: true 
            })
        });

        if (!response.ok) throw new Error('Erro ao salvar');

        const novaCategoria = await response.json();
        mostrarAlerta('Categoria adicionada com sucesso!');

        // Recarrega categorias
        await carregarCategoriasNoFormulario();

        // Seleciona a nova categoria
        selectElement.value = novaCategoria.id;

    } catch (erro) {
        mostrarAlerta('Erro ao adicionar categoria: ' + erro.message, true);
    }
}

async function abrirModalAdicionarAtividade(selectElement) {
    const cnae = prompt('Digite o código CNAE:');
    if (!cnae || !cnae.trim()) return;

    const descricao = prompt('Digite a descrição da atividade:');
    if (!descricao || !descricao.trim()) return;

    try {
        const response = await fetch('/api/atividades', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                cnae: cnae.trim(), 
                descricao: descricao.trim(), 
                secao: '', 
                divisao: '', 
                ativa: true 
            })
        });

        if (!response.ok) throw new Error('Erro ao salvar');

        const novaAtividade = await response.json();
        mostrarAlerta('Atividade adicionada com sucesso!');

        // Recarrega atividades
        await carregarAtividadesNoFormulario();

        // Seleciona a nova atividade
        selectElement.value = novaAtividade.id;

    } catch (erro) {
        mostrarAlerta('Erro ao adicionar atividade: ' + erro.message, true);
    }
}
