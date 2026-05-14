import requisitarDados from '../conection/query.js';
import logout from '../conection/logout.js'; 
import { abrirModal, fecharModal } from '../modules/modal.js';

// Expõe as funções para o escopo global (necessário para o 'onclick' no HTML)
window.logout = logout;
const moneyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

async function carregarProdutos() {
    try {
        const produtos = await requisitarDados('/api/produtos', 'GET');
        const tbody = document.getElementById('tabela-produtos-body');
        
        if (!tbody) return;

        if (produtos && produtos.length > 0) {
            tbody.innerHTML = ''; 

            produtos.forEach(produto => {
                const tr = document.createElement('tr');
                // Usamos uma classe de badge mais neutra para tipos de produto
                tr.innerHTML = `
                    <td>${produto.nomeProduto}</td>
                    <td><span class="badge" style="background-color: #444; color: var(--primary-color); border: 1px solid var(--primary-color);">${produto.tipoProduto}</span></td>
                    <td class="nowrap">${moneyFormatter.format(produto.valorUnitario)}</td>
                    <td>
                        <button class="btn btn-warning btn-table-action" onclick="window.prepararEdicao('${produto.idProduto}')" title="Editar">
                            <i class="fas fa-edit"></i> Editar
                        </button>
                        <button class="btn btn-danger btn-table-action" onclick="window.excluirProduto('${produto.idProduto}', '${produto.nomeProduto}')" title="Excluir">
                            <i class="fas fa-trash"></i> Excluir
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="4" class="empty-state">Nenhum produto cadastrado.</td></tr>';
        }
    } catch (error) {
        console.error('Erro ao carregar produtos:', error);
        const tbody = document.getElementById('tabela-produtos-body');
        if (tbody) tbody.innerHTML = '<tr><td colspan="4" class="error-message text-center">Erro ao carregar dados.</td></tr>';
    }
}

window.salvarProduto = async (event) => {
    event.preventDefault();
    const id = document.getElementById('produto-id').value;
    const dados = {
        nomeProduto: document.getElementById('produto-nome').value,
        tipoProduto: document.getElementById('produto-tipo').value,
        valorUnitario: document.getElementById('produto-valor').value // O backend já trata o parse de String para BigDecimal
    };

    const isEdicao = id && id.trim() !== "";
    try {
        await requisitarDados(isEdicao ? `/api/produtos/${id}` : '/api/produtos', isEdicao ? 'PUT' : 'POST', dados);
        alert(isEdicao ? 'Produto atualizado!' : 'Produto cadastrado!');
        fecharModal('modal-produto');
        carregarProdutos();
    } catch (error) {
        alert('Erro ao salvar produto.');
    }
};

window.prepararCriacao = () => {
    document.getElementById('form-produto')?.reset();
    if (document.getElementById('produto-id')) document.getElementById('produto-id').value = '';
    if (document.getElementById('modal-produto-titulo')) document.getElementById('modal-produto-titulo').textContent = "Novo Produto";
    abrirModal('modal-produto');
};

window.prepararEdicao = async (id) => {
    try {
        const produto = await requisitarDados(`/api/produtos/${id}`, 'GET');
        if (document.getElementById('produto-id')) document.getElementById('produto-id').value = produto.idProduto || id;
        document.getElementById('produto-nome').value = produto.nomeProduto;
        document.getElementById('produto-tipo').value = produto.tipoProduto;
        document.getElementById('produto-valor').value = produto.valorUnitario;
        
        document.getElementById('modal-produto-titulo').textContent = "Editar Produto";
        abrirModal('modal-produto');
    } catch (error) {
        alert('Erro ao carregar dados do produto.');
    }
};

window.excluirProduto = async (id, nome) => {
    if (confirm(`Deseja realmente excluir o produto "${nome}"?`)) {
        try {
            await requisitarDados(`/api/produtos/${id}`, 'DELETE');
            carregarProdutos(); // Recarrega a lista
        } catch (error) {
            console.error('Erro ao excluir:', error);
            alert('Erro ao excluir: Verifique se este produto possui vendas vinculadas.');
        }
    }
};

// Inicia a carga assim que o script é lido
document.addEventListener('DOMContentLoaded', carregarProdutos);