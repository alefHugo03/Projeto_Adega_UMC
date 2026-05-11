import requisitarDados from '../pesquisa/query.js';
import logout from '../common/logout.js'; 

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
                        <button class="btn-edit" onclick="window.prepararEdicao(${produto.idProduto})">Editar</button>
                        <button class="btn-delete" onclick="window.excluirProduto(${produto.idProduto})">Excluir</button>
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

// Funções de Ação expostas globalmente para o onclick do HTML

window.prepararCriacao = () => {
    // Aqui você pode abrir um modal ou redirecionar para um formulário
    alert('Funcionalidade de abrir formulário de cadastro (POST).');
};

window.prepararEdicao = (id) => {
    // Aqui você buscaria os dados do produto pelo ID e preencheria um formulário
    alert(`Editar produto ID: ${id}. Aqui você carregaria os dados para edição (PUT).`);
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