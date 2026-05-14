import requisitarDados from '../conection/query.js';
import { abrirModal, fecharModal } from '../modules/modal.js';

export async function carregarEstoque() {
    try {
        const estoques = await requisitarDados('/api/estoques', 'GET');
        const tbody = document.getElementById('tabela-estoque-body');
        if (!tbody) return;

        tbody.innerHTML = estoques.length > 0 ? '' : '<tr><td colspan="5" class="empty-state">Nenhum estoque registrado.</td></tr>';

        estoques.forEach(item => {
            const tr = document.createElement('tr');
            const statusClass = item.quantidade > 20 ? 'status-ok' : (item.quantidade > 5 ? 'status-warning' : 'status-danger');
            const statusTexto = item.quantidade > 20 ? 'OK' : (item.quantidade > 5 ? 'Baixo' : 'Crítico');

            tr.innerHTML = `
                <td>${item.produto.nomeProduto}</td>
                <td>${item.produto.tipoProduto}</td>
                <td><strong>${item.quantidade}</strong></td>
                <td><span class="badge ${statusClass}">${statusTexto}</span></td>
                <td>
                    <button class="btn btn-warning btn-table-action" 
                            onclick="window.prepararReposicao('${item.idEstoque}', '${item.produto.idProduto}', '${item.produto.nomeProduto}', ${item.quantidade})">
                        Atualizar
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Erro ao carregar estoque:', error);
    }
}

export function prepararReposicao(idEstoque, idProduto, nomeProduto, qtdAtual) {
    document.getElementById('estoque-id').value = idEstoque;
    document.getElementById('estoque-produto-id').value = idProduto;
    document.getElementById('estoque-produto-nome').value = nomeProduto;
    document.getElementById('estoque-quantidade').value = qtdAtual;
    abrirModal('modal-estoque');
}

export async function salvarEstoque(event) {
    event.preventDefault();
    const idEstoque = document.getElementById('estoque-id').value;
    const idProduto = document.getElementById('estoque-produto-id').value;
    const quantidade = parseInt(document.getElementById('estoque-quantidade').value);

    // Estrutura conforme esperado pelo EstoqueServiceImpl (EstoqueResponseDTO como input)
    const dados = {
        quantidade: quantidade,
        produto: { idProduto: parseInt(idProduto) }
    };

    try {
        await requisitarDados(`/api/estoques/${idEstoque}`, 'PUT', dados);
        alert('Estoque atualizado com sucesso!');
        fecharModal('modal-estoque');
        carregarEstoque();
    } catch (error) {
        alert('Erro ao atualizar estoque: ' + error.message);
    }
}