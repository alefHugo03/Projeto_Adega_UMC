import { abrirModal, fecharModal } from '../modules/modal.js';
import requisitarDados from '../conection/query.js';
import { handleAppError } from '../exception/exceptions.js';

/**
 * Verifica se o usuário logado tem perfil de administrador decodificando o JWT.
 * Ajustado para ser mais flexível com diferentes estruturas de claims.
 */
export const isAdmin = () => {
    const token = localStorage.getItem('jwt_token');
    if (!token) return false;
    try {
        // JWT usa base64url. Substituímos caracteres específicos para que o atob() funcione.
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const payload = JSON.parse(window.atob(base64));
        
        // Verifica em 'roles', 'role' ou 'authorities' (padrões comuns do Spring Security)
        const permissoes = payload.roles || payload.role || payload.authorities || [];
        
        console.log("Permissões detectadas:", permissoes); // Para te ajudar a debugar no console

        if (Array.isArray(permissoes)) {
            return permissoes.includes('ROLE_ADMIN') || permissoes.includes('ADMIN');
        }
        
        return permissoes === 'ROLE_ADMIN' || permissoes === 'ADMIN';
    } catch (e) { 
        console.error("Erro ao validar permissões:", e);
        return false; 
    }
};

export const prepararCriacaoUsuario = () => {
    const form = document.getElementById('form-usuario');
    if (form) form.reset();
    abrirModal('modal-usuario');
};

export const salvarUsuario = async (e) => {
    e.preventDefault();
    const dados = {
        nome: document.getElementById('usuario-nome').value,
        email: document.getElementById('usuario-email').value,
        senha: document.getElementById('usuario-senha').value
    };

    try {
        await requisitarDados('/api/usuarios', 'POST', dados);
        alert('Usuário criado com sucesso!');
        fecharModal('modal-usuario');
        await carregarUsuarios();
    } 
    catch (error) {
        handleAppError(error);
    }
};

export const desativarUsuario = async (id) => {
    if (confirm('Deseja realmente desativar este usuário?')) {
        try {
            await requisitarDados(`/api/usuarios/${id}`, 'DELETE');
            alert('Usuário desativado com sucesso!');
            await carregarUsuarios();
        } catch (error) {
            handleAppError(error);
        }
    }
};

export async function carregarUsuarios() {
    try {
        const users = await requisitarDados('/api/usuarios', 'GET');
        const tbody = document.getElementById('tabela-usuarios-body');
        if (!tbody) return;

        tbody.innerHTML = users.map(u => `
            <tr>
                <td>${u.nome}</td>
                <td>${u.email}</td>
                <td>${u.role === 'ROLE_ADMIN' ? 'Admin' : 'Vendedor'}</td>
                <td>
                    <span class="badge ${u.active ? 'status-ok' : 'status-danger'}">${u.active ? 'Ativo' : 'Desativado'}</span>
                </td>
                <td>
                    ${(u.role !== 'ROLE_ADMIN' && u.active) ? `<button class="btn btn-danger btn-table-action" onclick="window.desativarUsuario(${u.id})">Desativar</button>` : '-'}
                </td>
            </tr>`).join('');
    } catch (error) {
        handleAppError(error);
    }
}