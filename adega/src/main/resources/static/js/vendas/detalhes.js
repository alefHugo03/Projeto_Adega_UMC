import requisitarDados from '../conection/query.js';
import { abrirModal } from '../modules/modal.js';

export async function verDetalhesVenda(vendaId) {
    try {
        const itens = await requisitarDados(`/api/itemvendas/venda/${vendaId}`, 'GET');
        const listaItens = document.getElementById('lista-itens-venda');
        const spanId = document.getElementById('detalhe-id-venda');

        if (spanId) spanId.innerText = vendaId;
        if (listaItens) {
            listaItens.innerHTML = '';
            if (itens && itens.length > 0) {
                itens.forEach(item => {
                    const div = document.createElement('div');
                    div.className = 'item-venda-card';
                    div.innerHTML = `
                        <p><strong>Produto:</strong> ${item.produto?.nomeProduto || item.nomeProduto || 'N/A'}</p>
                        <p><strong>Qtd:</strong> ${item.quantidadeVendida || item.quantidade || 0} unid.</p>
                    `;
                    listaItens.appendChild(div);
                });
            } else {
                listaItens.innerHTML = '<p class="empty-state">Esta venda não possui itens registrados.</p>';
            }
        }

        const modalId = 'modal-detalhes'; // Verifique se este ID existe no seu HTML
        if (document.getElementById(modalId)) {
            abrirModal(modalId);
        } else {
            console.error(`Erro: Elemento com ID "${modalId}" não encontrado no HTML.`);
            alert('Erro interno: Modal de detalhes não configurado no HTML.');
        }
    } catch (error) {
        console.error('Erro ao buscar itens:', error);
        const msg = error.message.includes('404') ? 'A API de itens não foi encontrada (404).' : 'Erro ao buscar itens da venda.';
        alert(msg);
    }
}