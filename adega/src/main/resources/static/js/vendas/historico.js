import requisitarDados from '../conection/query.js';
import { handleAppError } from '../exception/exceptions.js';

/**
 * 
 * Função para formatações da pagina da venda 
 */
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
                
                // Formata a data para algo mais amigável: DD/MM/AAAA HH:mm
                const dataFormatada = venda.dataVenda 
                    ? new Date(venda.dataVenda).toLocaleString('pt-BR') 
                    : 'N/A';

                // Define o visual do status baseado no campo isActive (venda.active)
                const statusBadge = venda.active 
                    ? '<span class="badge status-ok">Realizada</span>' 
                    : '<span class="badge status-danger">Cancelada</span>';
                
                const infoPagamento = venda.quantidadeParcelas > 1 
                    ? `${venda.formaPagamento} (${venda.quantidadeParcelas}x)` 
                    : venda.formaPagamento;

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>#${idVenda || '---'}</td>
                    <td>${dataFormatada}</td>
                    <td>${infoPagamento}</td>
                    <td class="text-right">${moneyFormatter.format(venda.valorTotal || 0)}</td>
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