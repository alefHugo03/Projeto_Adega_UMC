import { isAdmin } from '../usuario/acoes.js';
import { initSessionMonitor } from './session.js';

export function loadSidebar() {
    const container = document.getElementById('sidebar-container');
    if (!container) return;

    const currentPath = window.location.pathname;
    const isUserAdmin = isAdmin();

    // 1. Extrair e decodificar os dados do utilizador logado a partir do JWT Token
    const token = localStorage.getItem('jwt_token');
    let usuarioLogado = 'Utilizador'; // Nome padrão caso falhe
    
    if (token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            // Tenta obter o 'sub' (padrão JWT), 'email' ou 'nome'. Ajuste conforme o seu backend.
            usuarioLogado = payload.sub || payload.email || payload.nome || 'Utilizador';
        } catch (error) {
            console.error("Erro ao decodificar os dados do utilizador:", error);
        }
    }

    // Função auxiliar para definir a classe 'active' baseada na URL atual
    const getActiveClass = (path) => currentPath.includes(path) ? 'active' : '';

    const sidebarHTML = `
        <aside class="sidebar">
            <div>
                <div class="sidebar-header">
                    <h2>Adego Systems</h2>
                </div>
                <nav class="sidebar-nav">
                    <a href="/home" class="${getActiveClass('/home')}">
                        <i class="fas fa-home"></i> Dashboard
                    </a>
                    <a href="/produtos" class="${getActiveClass('/produtos')}">
                        <i class="fas fa-wine-bottle"></i> Produtos
                    </a>
                    <a href="/estoque" class="${getActiveClass('/estoque')}">
                        <i class="fas fa-boxes"></i> Estoque
                    </a>
                    <a href="/vendas" class="${getActiveClass('/vendas')}">
                        <i class="fas fa-shopping-cart"></i> Vendas
                    </a>
                    <a href="/usuarios" id="menu-usuarios" 
                       class="${getActiveClass('/usuarios')}" 
                       style="display: ${isUserAdmin ? 'block' : 'none'}">
                        <i class="fas fa-users"></i> Usuários
                    </a>
                </nav>
            </div>
            
            <div class="sidebar-footer">
                <div class="user-info">
                    <i class="fas fa-user-circle"></i>
                    <span class="user-name" title="${usuarioLogado}">${usuarioLogado}</span>
                </div>
                <button class="btn-logout-sidebar" onclick="window.logout()">
                    <i class="fas fa-sign-out-alt"></i> Sair
                </button>
            </div>
        </aside>
    `;

    container.innerHTML = sidebarHTML;

    // Inicia o monitoramento de inatividade assim que o sistema carregar a navegação
    initSessionMonitor();
}