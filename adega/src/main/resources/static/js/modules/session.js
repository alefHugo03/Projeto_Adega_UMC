import { abrirModal, fecharModal } from './modal.js';

let warningTimer;
let expirationTimer;
let countdownInterval; // Controla o relógio na tela
let sessionEndTime;    // Guarda a hora exata em que a sessão vai acabar

// Tempos em milissegundos
const TIME_LIMIT = 10 * 60 * 1000; // 10 minutos
const WARNING_TIME = 1 * 60 * 1000; // 1 minuto (Aparece faltando 9 minutos)
// Nota: Se quiser que o aviso apareça faltando 1 minuto para acabar, mude WARNING_TIME para: 9 * 60 * 1000

/**
 * Inicia os temporizadores da sessão.
 */
export function initSessionMonitor() {
    // Garante que o HTML dos modais de sessão exista na página
    injectSessionModals();
    
    // Se não houver token, não inicia o monitoramento (usuário não logado)
    if (!localStorage.getItem('jwt_token')) return;

    // Reinicia o timer sempre que houver interação do usuário
    setupActivityListeners();
    
    console.log("Monitor de sessão iniciado. Aguardando inatividade...");
    startTimers();
}

/**
 * Atualiza o texto do cronômetro no HTML
 */
function atualizarContador() {
    const agora = Date.now();
    const tempoRestante = sessionEndTime - agora;

    // Se o tempo acabou, trava no 00:00 e para o relógio
    if (tempoRestante <= 0) {
        clearInterval(countdownInterval);
        const contadorEl = document.getElementById('contador-tempo');
        if (contadorEl) contadorEl.textContent = "00:00";
        return;
    }

    // Converte milissegundos para minutos e segundos
    const minutos = Math.floor(tempoRestante / 60000);
    const segundos = Math.floor((tempoRestante % 60000) / 1000);

    // Formata para ficar com dois dígitos (ex: 09:05)
    const textoFormatado = `${String(minutos).padStart(2, '0')}:${String(segundos).padStart(2, '0')}`;

    // Procura o elemento na tela e atualiza o texto
    const contadorEl = document.getElementById('contador-tempo');
    if (contadorEl) {
        contadorEl.textContent = textoFormatado;
    }
}

/**
 * Inicia e reseta todos os contadores
 */
function startTimers() {
    // Limpa timers anteriores para evitar duplicidade
    if (warningTimer) clearTimeout(warningTimer);
    if (expirationTimer) clearTimeout(expirationTimer);
    if (countdownInterval) clearInterval(countdownInterval); // Limpa o relógio anterior

    // Define a hora exata em que a sessão vai morrer
    sessionEndTime = Date.now() + TIME_LIMIT;

    // Inicia o cronômetro visual rodando a cada 1 segundo
    countdownInterval = setInterval(atualizarContador, 1000);
    atualizarContador(); // Chama uma vez imediatamente para atualizar a tela na hora

    // Timer para mostrar o aviso de inatividade
    warningTimer = setTimeout(() => {
        if (localStorage.getItem('jwt_token')) abrirModal('modal-sessao-aviso');
    }, WARNING_TIME);

    // Timer para o fim da sessão (10 min)
    expirationTimer = setTimeout(() => {
        fecharModal('modal-sessao-aviso'); // Fecha o aviso caso ainda esteja aberto
        abrirModal('modal-sessao-expirada');
        
        // Limpa dados locais por segurança
        localStorage.removeItem('jwt_token');
        // Remove o cookie para sincronizar com o backend
        document.cookie = "jwt_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    }, TIME_LIMIT);
}

function setupActivityListeners() {
    const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'click'];
    let throttle = false;
    
    events.forEach(name => {
        document.addEventListener(name, () => {
            if (throttle) return;
            
            // NOVA REGRA: Se o aviso estiver aberto, ignoramos a atividade de fundo.
            // Isso obriga o usuário a interagir com os botões, o "X" ou clicar fora.
            const aviso = document.getElementById('modal-sessao-aviso');
            if (aviso && aviso.style.display === 'flex') {
                return; // Aborta e não reseta os timers
            }
            
            throttle = true;
            setTimeout(() => {
                startTimers();
                throttle = false;
            }, 2000); 
        }, { passive: true });
    });
}

function injectSessionModals() {
    if (document.getElementById('modal-sessao-aviso')) return;

    // HTML do modal atualizado com o botão X e o SPAN do cronômetro
    const modalHTML = `
        <div id="modal-sessao-aviso" class="modal">
            <div class="modal-content card card-sm" style="position: relative;">
                <span id="btn-x-aviso" style="position: absolute; top: 10px; right: 15px; cursor: pointer; font-size: 1.5rem; font-weight: bold;">&times;</span>
                <h2>⚠️ Atenção</h2>
                <p>Deseja continuar logado? Sua sessão expira em: <br><strong id="contador-tempo" style="color: red; font-size: 2rem;">10:00</strong></p>
                <div class="d-flex gap-2">
                    <button id="btn-continuar-sessao" class="btn btn-primary btn-block">Continuar</button>
                    <button id="btn-fechar-aviso" class="btn btn-danger btn-block">Sair</button>
                </div>
            </div>
        </div>

        <div id="modal-sessao-expirada" class="modal">
            <div class="modal-content card card-sm">
                <h2>🛑 Sessão Expirada</h2>
                <p>Sua sessão expirou por inatividade. Clique abaixo para fazer login novamente.</p>
                <button class="btn btn-primary btn-block" onclick="window.location.href='/login'">Fazer Login</button>
            </div>
        </div>
    `;

    const div = document.createElement('div');
    div.innerHTML = modalHTML;
    document.body.appendChild(div);

    // Evento: Continuar pelo botão
    document.getElementById('btn-continuar-sessao').addEventListener('click', () => {
        window.location.reload();
        fecharModal('modal-sessao-aviso');
        startTimers(); 
    });

    // Evento: Continuar fechando pelo "X"
    document.getElementById('btn-x-aviso').addEventListener('click', () => {
        fecharModal('modal-sessao-aviso');
        startTimers(); 
    });

    // Evento: Continuar clicando FORA da caixa (no fundo escuro)
    document.getElementById('modal-sessao-aviso').addEventListener('mousedown', (event) => {
        // Se o clique foi exatamente no fundo (e não nos elementos filhos da modal-content)
        if (event.target.id === 'modal-sessao-aviso') {
            fecharModal('modal-sessao-aviso');
            startTimers();
        }
    });

    // Evento: Sair pelo botão de sair
    document.getElementById('btn-fechar-aviso').addEventListener('click', () => {
        fecharModal('modal-sessao-aviso');
        if (typeof window.logout === 'function') {
            window.logout();
        } else {
            window.location.href = '/login';
        }
    });
}

// Expõe para uso global se necessário
window.initSessionMonitor = initSessionMonitor;

// Função para teste imediato (chame no console do navegador para ver o popup)
window.testarPopupSessao = () => {
    injectSessionModals();
    abrirModal('modal-sessao-aviso');
};