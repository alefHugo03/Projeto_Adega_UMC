import requisitarDados from '../conection/query.js';

// Carrega e renderiza uma listagem alternativa do estoque
async function carregarEstoque() {
    try {
        const estoques = await requisitarDados('/api/estoques', 'GET');
        const tbody = document.getElementById('tabela-estoque-body');
        if (!tbody) return;
        
        if (estoques && estoques.length > 0) {
            tbody.innerHTML = ''; 
            estoques.forEach(item => {
                let statusClass = 'status-ok';
                let statusText = 'Em Dia';
                if (item.quantidade <= 0) {
                    statusClass = 'status-danger';
                    statusText = 'Esgotado';
                } else if (item.quantidade < 5) {
                    statusClass = 'status-warning';
                    statusText = 'Baixo';
                }

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${item.produto ? item.produto.nomeProduto : 'Produto não identificado'}</td>
                    <td><strong>${item.quantidade}</strong> unid.</td>
                    <td><span class="badge ${statusClass}">${statusText}</span></td>
                    <td>
                        <button class="btn btn-primary" style="padding: 5px 10px; font-size: 0.8rem;" onclick="window.prepararEdicao(${item.id})">Ajustar</button>
                        <button class="btn btn-danger" style="padding: 5px 10px; font-size: 0.8rem;" onclick="window.excluirEstoque(${item.id})">Remover</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="4" class="empty-state">Nenhum item em estoque encontrado.</td></tr>';
        }
    } catch (error) {
        console.error('Erro ao carregar estoque:', error);
        const tbody = document.getElementById('tabela-estoque-body');
        if (tbody) tbody.innerHTML = '<tr><td colspan="4" class="empty-state">Erro ao carregar dados do estoque.</td></tr>';
    }
}

export default carregarEstoque;
