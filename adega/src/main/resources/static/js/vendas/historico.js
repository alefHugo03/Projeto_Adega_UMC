import requisitarDados from '../../conection/query.js';
import { handleAppError } from '../../exception/exceptions.js';

// Carrega o histórico de vendas e renderiza a tabela de vendas na página.
async function carregarHistoricoVendas() {
    try {
        const vendas = await requisitarDados('/api/vendas', 'GET');
        const tbody = document.getElementById('tabela-vendas-body');
        if (!tbody) return;
        
        tbody.innerHTML = '';

        if (vendas && vendas.length > 0) {
            const moneyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

            vendas.forEach(venda => {
                const idVenda = venda.idVenda || venda.id;
                const dataFormatada = venda.dataVenda 
                    ? new Date(venda.dataVenda).toLocaleString('pt-BR') 
                    : 'N/A';

                const statusBadge = venda.active 
                    ? '<span class="badge status-ok">Realizada</span>' 
                    : '<span class="badge status-danger">Cancelada</span>';
                
                const isRetirada = venda.formaPagamento === 'RETIRADA_ADMIN';
                
                const infoPagamento = isRetirada 
                    ? '<span style="color: #e74c3c; font-weight: bold;">[BAIXA ADMIN]</span>'
                    : (venda.quantidadeParcelas > 1 
                    ? `${venda.formaPagamento} (${venda.quantidadeParcelas}x)` 
                    : venda.formaPagamento);

                const valorDisplay = isRetirada ? '---' : moneyFormatter.format(venda.valorTotal || 0);

                const tr = document.createElement('tr');
                if (isRetirada) tr.style.backgroundColor = 'rgba(231, 76, 60, 0.05)';
                
                tr.innerHTML = `
                    <td>#${idVenda || '---'}</td>
                    <td>${dataFormatada}</td>
                    <td>${infoPagamento}</td>
                    <td class="text-right">${valorDisplay}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <button class="btn btn-info btn-table-action" onclick="window.verDetalhesVenda('${idVenda}')">Itens</button>
                        <button class="btn btn-warning btn-table-action" onclick="window.prepararEdicaoVenda('${idVenda}')">Editar</button>
                        <button class="btn btn-danger btn-table-action" onclick="window.excluirVenda('${idVenda}')">Excluir</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-state">Nenhuma venda encontrada.</td></tr>';
        }
    } catch (error) {
        handleAppError(error);
        const tbody = document.getElementById('tabela-vendas-body');
        if (tbody) tbody.innerHTML = '<tr><td colspan="5" class="empty-state">Erro ao carregar dados.</td></tr>';
    }
}

export default carregarHistoricoVendas;
