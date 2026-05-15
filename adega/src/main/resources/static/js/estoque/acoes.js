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
                            onclick="window.prepararEntradaEstoque('${item.idEstoque}', '${item.produto.idProduto}', '${item.produto.nomeProduto}', ${item.quantidade})">
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

/**
 * Prepara o modal para uma nova entrada de um produto que pode ou não estar na lista
 */
export async function iniciarNovaEntrada() {
    document.getElementById('form-estoque').reset();
    document.getElementById('estoque-id').value = '';
    
    // Alterna visibilidade para permitir seleção de produto
    document.getElementById('estoque-produto-id').style.display = 'none';
    document.getElementById('estoque-produto-selecao').style.display = 'block';
    document.getElementById('estoque-produto-nome-fixo').style.display = 'none';

    await carregarProdutosNoSelect();
    abrirModal('modal-estoque');
}

export function prepararEntradaEstoque(idEstoque, idProduto, nomeProduto, qtdAtual) {
    document.getElementById('estoque-id').value = idEstoque || '';
    document.getElementById('estoque-produto-id').value = idProduto;
    
    // Para atualização, mostramos apenas o nome (não permite trocar o produto)
    const displayNome = document.getElementById('estoque-produto-nome-fixo');
    displayNome.value = nomeProduto;
    displayNome.style.display = 'block';
    document.getElementById('estoque-produto-selecao').style.display = 'none';

    document.getElementById('estoque-quantidade').value = '';
    abrirModal('modal-estoque');
}

async function carregarProdutosNoSelect() {
    const select = document.getElementById('estoque-produto-selecao');
    try {
        const produtos = await requisitarDados('/api/produtos', 'GET');
        select.innerHTML = '<option value="">Selecione um produto...</option>';
        produtos.forEach(p => {
            select.innerHTML += `<option value="${p.idProduto}">${p.nomeProduto}</option>`;
        });
    } catch (e) { console.error("Erro ao carregar produtos para entrada", e); }
}

export async function salvarEstoque(event) {
    event.preventDefault();
    const idProdutoFixo = document.getElementById('estoque-produto-id').value;
    const idProdutoSelect = document.getElementById('estoque-produto-selecao').value;
    const idProduto = idProdutoFixo || idProdutoSelect;
    const quantidadeAdicional = parseInt(document.getElementById('estoque-quantidade').value);

    const dados = {
        quantidade: quantidadeAdicional,
        produto: { idProduto: parseInt(idProduto) }
    };

    try {
        // Chamamos o POST para processar a "Entrada" (soma no backend)
        await requisitarDados('/api/estoques', 'POST', dados);
        alert('Entrada de estoque realizada com sucesso!');
        fecharModal('modal-estoque');
        carregarEstoque();
    } catch (error) {
        alert('Erro ao atualizar estoque: ' + error.message);
    }
}