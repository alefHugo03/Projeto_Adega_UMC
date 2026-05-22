import { abrirModal, fecharModal } from './modal.js';

let warningTimer;
let expirationTimer;

// Tempos em milissegundos
const TIME_LIMIT = 10 * 60 * 1000; // 10 minutos
const WARNING_TIME = 1 * 60 * 1000; // 1 minuto

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

function startTimers() {
    // Limpa timers anteriores para evitar duplicidade
    if (warningTimer) clearTimeout(warningTimer);
    if (expirationTimer) clearTimeout(expirationTimer);

    // Timer para mostrar o aviso de inatividade (1 min)
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
    // Lista de eventos que indicam que o usuário está ativo
    const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'click'];
    let throttle = false;
    
    events.forEach(name => {
        document.addEventListener(name, () => {
            if (throttle) return;
            
            throttle = true;
            setTimeout(() => {
                // Se o usuário mexer o mouse e o aviso estiver aberto, nós fechamos o aviso e resetamos o tempo
                const aviso = document.getElementById('modal-sessao-aviso');
                if (aviso && aviso.style.display === 'flex') fecharModal('modal-sessao-aviso');
                
                startTimers();
                throttle = false;
            }, 2000); // Só processa atividade a cada 2 segundos para não sobrecarregar
        }, { passive: true });
    });
}

function injectSessionModals() {
    // Evita criar duplicatas se a função for chamada múltiplas vezes
    if (document.getElementById('modal-sessao-aviso')) return;

    const modalHTML = `
        <div id="modal-sessao-aviso" class="modal">
            <div class="modal-content card card-sm">
                <h2>⚠️ Atenção</h2>
                <p>Sua sessão está inativa há 1 minuto. Deseja continuar logado?</p>
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

    // Eventos dos botões
    document.getElementById('btn-continuar-sessao').addEventListener('click', () => {
        fecharModal('modal-sessao-aviso');
        startTimers(); // Reinicia a contagem no Front-end
        
        // Dica: Se quiser que o cookie no backend também resete, 
        // você precisaria fazer uma chamada dummy para a API aqui.
    });

    document.getElementById('btn-fechar-aviso').addEventListener('click', () => {
        fecharModal('modal-sessao-aviso');
        // Se a função global logout estiver disponível, executa o fluxo completo de saída
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