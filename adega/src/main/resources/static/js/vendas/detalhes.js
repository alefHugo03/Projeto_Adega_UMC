import requisitarDados from '../../conection/query.js';
import { abrirModal } from '../../modules/modal.js';
import { handleAppError } from '../../exception/exceptions.js';

// Busca os itens de uma venda e exibe no modal de detalhes.
export async function verDetalhesVenda(vendaId) {
    try {
        const itens = await requisitarDados(`/api/itemvendas/venda/${vendaId}`, 'GET');
        const venda = await requisitarDados(`/api/vendas/${vendaId}`, 'GET');
        const listaItens = document.getElementById('lista-itens-venda');
        const spanId = document.getElementById('detalhe-id-venda');
        const pPagamento = document.getElementById('detalhe-pagamento-venda');
        const pVendedor = document.getElementById('detalhe-vendedor-venda');
        const pMotivo = document.getElementById('detalhe-motivo-venda');

        if (spanId) spanId.innerText = vendaId;
        if (pPagamento && venda) pPagamento.innerText = venda.formaPagamento || '';
        if (pVendedor && venda && venda.user) pVendedor.innerText = venda.user.nome || '';
        if (pMotivo && venda) pMotivo.innerText = venda.motivo || '';
        if (listaItens) {
            listaItens.innerHTML = '';
            if (itens && itens.length > 0) {
                const moneyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

                itens.forEach(item => {
                    const valorUnitario = item.produto?.valorUnitario || 0;
                    const qtd = item.quantidadeVendida || item.quantidade || 0;
                    const subtotal = valorUnitario * qtd;

                    const div = document.createElement('div');
                    div.className = 'item-venda-card';
                    div.innerHTML = `
                        <p><strong>Produto:</strong> ${item.produto?.nomeProduto || item.nomeProduto || 'N/A'}</p>
                        <p><strong>Preço Unit.:</strong> ${moneyFormatter.format(valorUnitario)}</p>
                        <p><strong>Quantidade:</strong> ${qtd} unid.</p>
                        <p><strong>Subtotal:</strong> ${moneyFormatter.format(subtotal)}</p>
                    `;
                    listaItens.appendChild(div);
                });
            } else {
                listaItens.innerHTML = '<p class="empty-state">Esta venda não possui itens registrados.</p>';
            }
        }

        const modalId = 'modal-detalhes';
        if (document.getElementById(modalId)) {
            abrirModal(modalId);
        } else {
            console.error(`Erro: Elemento com ID "${modalId}" não encontrado no HTML.`);
            alert('Erro interno: Modal de detalhes não configurado no HTML.');
        }
    } catch (error) {
        handleAppError(error);
    }
}
