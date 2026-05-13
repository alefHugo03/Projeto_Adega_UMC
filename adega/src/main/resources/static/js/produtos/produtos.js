import requisitarDados from '../conection/query.js';
import logout from '../conection/logout.js'; 
import { abrirModal } from '../modules/modal.js';

// Expõe as funções para o escopo global (necessário para o 'onclick' no HTML)
window.logout = logout;

async function carregarProdutos() {
    // Supondo que seu endpoint de listagem de produtos seja /api/produtos
    try {
        const produtos = await requisitarDados('/api/produtos', 'GET');

        if (produtos && produtos.length > 0) {
            const tbody = document.getElementById('tabela-produtos-body');
            tbody.innerHTML = ''; // Limpa a tabela antes de preencher

            const formatter = new Intl.NumberFormat('pt-BR', {
                style: 'currency',
                currency: 'BRL',
            });

            produtos.forEach(produto => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${produto.nomeProduto}</td>
                    <td>${produto.tipoProduto}</td>
                    <td>${formatter.format(produto.valorUnitario)}</td>
                    <td>
                        <button class="btn btn-primary btn-table-action" onclick="window.prepararEdicao('${produto.idProduto}')">Editar</button>
                        <button class="btn btn-danger btn-table-action" onclick="window.excluirProduto('${produto.idProduto}')">Excluir</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            document.getElementById('tabela-produtos-body').innerHTML = '<tr><td colspan="4">Nenhum produto encontrado.</td></tr>';
        }
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        document.getElementById('tabela-produtos-body').innerHTML = '<tr><td colspan="4">Erro ao carregar produtos.</td></tr>';
    }
}

window.prepararCriacao = () => {
    const form = document.getElementById('form-produto');
    if (form) form.reset();
    const idCampo = document.getElementById('produto-id');
    if (idCampo) idCampo.value = '';
    const titulo = document.getElementById('modal-produto-titulo');
    if (titulo) titulo.textContent = "Novo Produto";
    if (typeof abrirModal === 'function') abrirModal('modal-produto');
};

window.prepararEdicao = async (id) => {
    try {
        const produto = await requisitarDados(`/api/produtos/${id}`, 'GET');
        document.getElementById('produto-id').value = produto.idProduto || produto.id || id;
        document.getElementById('produto-nome').value = produto.nomeProduto;
        document.getElementById('produto-tipo').value = produto.tipoProduto;
        document.getElementById('produto-valor').value = produto.valorUnitario;
        
        document.getElementById('modal-produto-titulo').textContent = "Editar Produto";
        if (typeof abrirModal === 'function') abrirModal('modal-produto');
    } catch (error) {
        alert('Erro ao carregar dados do produto.');
    }
};

window.excluirProduto = async (id) => {
    if (confirm('Tem certeza que deseja excluir este produto?')) {
        try {
            // Utiliza o query.js para a deleção
            await requisitarDados(`/api/produtos/${id}`, 'DELETE');
            alert('Produto excluído com sucesso!');
            carregarProdutos(); // Recarrega a lista
        } catch (error) {
            console.error('Erro ao excluir:', error);
            alert('Não foi possível excluir o produto.');
        }
    }
};

// Inicia a carga assim que o script é lido
document.addEventListener('DOMContentLoaded', carregarProdutos);