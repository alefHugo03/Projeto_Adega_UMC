import { isAdmin } from '../usuario/acoes.js';

export function loadSidebar() {
    const container = document.getElementById('sidebar-container');
    if (!container) return;

    const currentPath = window.location.pathname;
    const isUserAdmin = isAdmin();

    // Função auxiliar para definir a classe 'active' baseada na URL atual
    const getActiveClass = (path) => currentPath.includes(path) ? 'active' : '';

    const sidebarHTML = `
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>Adego Systems</h2>
            </div>
            <nav class="sidebar-nav">
                <a href="/home" class="${getActiveClass('/home')}">Dashboard</a>
                <a href="/produtos" class="${getActiveClass('/produtos')}">Produtos</a>
                <a href="/estoque" class="${getActiveClass('/estoque')}">Estoque</a>
                <a href="/vendas" class="${getActiveClass('/vendas')}">Vendas</a>
                <a href="/usuarios" id="menu-usuarios" 
                   class="${getActiveClass('/usuarios')}" 
                   style="display: ${isUserAdmin ? 'block' : 'none'}">
                   Usuários
                </a>
                <button class="btn-logout-sidebar" onclick="window.logout()">Sair</button>
            </nav>
        </aside>
    `;

    container.innerHTML = sidebarHTML;
}
