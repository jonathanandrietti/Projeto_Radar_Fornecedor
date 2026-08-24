console.log('script.js carregado!');
const configuracao = document.body.dataset;
const endpoint = `/api/${configuracao.entidade}`;
let registros = [];

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
        logarAutomaticamente("Você ficou inativo por mais de 30 minutos.");
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

document.addEventListener('DOMContentLoaded', () => {
    console.log('DOMContentLoaded disparado');
    console.log('container user-info:', document.getElementById('user-info'));
    carregar();
    exibirInfoUsuario();
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

function filtrar() {
    const termo = (document.getElementById('busca').value || '').toLowerCase();
    const exibidos = registros.filter(item => [item.nome, item.cnpj, item.status, item.cidade, item.estado]
        .some(valor => String(valor || '').toLowerCase().includes(termo)));
    const linhas = document.getElementById('linhas');
    linhas.innerHTML = exibidos.length ? exibidos.map(linha).join('') : '<tr><td colspan="7" class="p-10 text-center text-slate-500">Nenhum cadastro encontrado.</td></tr>';
}

function linha(item) {
    const endereco = [item.logradouro, item.numero].filter(Boolean).join(', ');
    const cidade = [item.cidade, item.estado].filter(Boolean).join('/');
    return `<tr class="hover:bg-slate-50"><td class="p-4 font-medium">${item.id}</td><td class="p-4 font-semibold">${escapar(item.nome)}</td><td class="p-4">${formatarCnpj(item.cnpj)}</td><td class="p-4"><span class="rounded-full bg-slate-100 px-2 py-1 text-xs font-bold">${formatarStatus(item.status)}</span></td><td class="p-4">${Number(item.pontuacaoRisco || 0).toLocaleString('pt-BR', {minimumFractionDigits: 1})}</td><td class="p-4 text-slate-600">${escapar(endereco || 'Não informado')}<br><span class="text-xs">${escapar(cidade)}</span></td><td class="p-4 text-center"><button onclick="editar(${item.id})" class="mr-2 text-sky-700" title="Editar"><i class="fa-solid fa-pen"></i></button><button onclick="excluirRegistro(${item.id})" class="text-rose-700" title="Excluir"><i class="fa-solid fa-trash"></i></button></td></tr>`;
}

function atualizarIndicadores() {
    const total = registros.length;
    const media = total ? registros.reduce((s, i) => s + Number(i.pontuacaoRisco || 0), 0) / total : 0;
    document.getElementById('total').textContent = total;
    document.getElementById('analise').textContent = registros.filter(i => i.status === 'EM_ANALISE').length;
    document.getElementById('media').textContent = media.toLocaleString('pt-BR', {minimumFractionDigits: 1, maximumFractionDigits: 1});
    document.getElementById('criticos').textContent = registros.filter(i => Number(i.pontuacaoRisco || 0) >= 7).length;
}

function abrirFormulario() {
    document.getElementById('formulario').reset(); document.getElementById('id').value = '';
    document.getElementById('titulo-form').textContent = `Cadastrar ${configuracao.singular.toLowerCase()}`;
    document.getElementById('modal').classList.remove('hidden');
}
function fecharFormulario() { document.getElementById('modal').classList.add('hidden'); }
function editar(id) {
    const item = registros.find(i => i.id === id); if (!item) return;
    ['id','codCidade','nome','status','cep','logradouro','numero','complemento','bairro','cidade','estado'].forEach(campo => document.getElementById(campo).value = item[campo] || '');
    document.getElementById('cnpj').value = formatarCnpj(item.cnpj); document.getElementById('pontuacao').value = item.pontuacaoRisco || 0;
    document.getElementById('titulo-form').textContent = `Editar ${configuracao.singular.toLowerCase()}`;
    document.getElementById('modal').classList.remove('hidden');
}
async function salvar(evento) {
    evento.preventDefault();
    const id = document.getElementById('id').value;
    const dados = Object.fromEntries(['nome','status','cep','logradouro','numero','complemento','bairro','cidade','estado','latitude','longitude'].map(campo => [campo, document.getElementById(campo).value.trim()]));
    dados.codCidade = document.getElementById('codCidade').value || null;
    dados.cnpj = document.getElementById('cnpj').value.replace(/\D/g, ''); dados.pontuacaoRisco = Number(document.getElementById('pontuacao').value || 0);
    try {
        const resposta = await fetch(id ? `${endpoint}/${id}` : endpoint, {method: id ? 'PUT' : 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(dados)});
        if (!resposta.ok) throw new Error('Não foi possível salvar. Verifique se o CNPJ é válido e não está duplicado.');
        fecharFormulario(); mostrarAlerta(`${configuracao.singular} salvo com sucesso.`); carregar();
    } catch (erro) { mostrarAlerta(erro.message, true); }
}
async function excluirRegistro(id) {
    if (!confirm(`Excluir este ${configuracao.singular.toLowerCase()}? Esta ação não pode ser desfeita.`)) return;
    try { const resposta = await fetch(`${endpoint}/${id}`, {method:'DELETE'}); if (!resposta.ok) throw new Error('Não foi possível excluir o cadastro.'); mostrarAlerta('Cadastro excluído.'); carregar(); } catch (erro) { mostrarAlerta(erro.message, true); }
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

function mascaraCep(input) {
    let valor = input.value.replace(/\D/g, '').slice(0, 8);
    input.value = valor.replace(/^(\d{5})(\d)/, '$1-$2');
}
function mascaraCnpj(input) { let valor = input.value.replace(/\D/g, '').slice(0,14); input.value = valor.replace(/^(\d{2})(\d)/,'$1.$2').replace(/^(\d{2}\.\d{3})(\d)/,'$1.$2').replace(/(\d{3}\.)(\d{3})(\d)/,'$1$2/$3').replace(/(\d{4}\/)(\d{2})$/,'$1$2'); }
function formatarCnpj(valor) { const cnpj = String(valor || '').replace(/\D/g,''); return cnpj.length === 14 ? cnpj.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5') : valor || ''; }
function formatarStatus(status) { return ({EM_ANALISE:'Em análise', APROVADO:'Aprovado', REJEITADO:'Rejeitado', SUSPENSO:'Suspenso'})[status] || status || 'Em análise'; }
function escapar(valor) { const elemento = document.createElement('span'); elemento.textContent = valor || ''; return elemento.innerHTML; }
function mostrarAlerta(mensagem, erro = false) { const alerta = document.getElementById('alerta'); alerta.textContent = mensagem; alerta.className = `mb-5 rounded-lg border px-4 py-3 text-sm ${erro ? 'border-rose-200 bg-rose-50 text-rose-800' : 'border-emerald-200 bg-emerald-50 text-emerald-800'}`; alerta.classList.remove('hidden'); }
