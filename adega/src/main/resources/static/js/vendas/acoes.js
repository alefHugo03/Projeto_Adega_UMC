import requisitarDados from '../conection/query.js';
import { abrirModal, fecharModal } from '../modules/modal.js';
import { handleAppError } from '../exception/exceptions.js';
// import excluir from '../conection/excluir.js'; // Este import não é utilizado e pode ser removido

// Variáveis globais para controle de paginação
let currentPage = 0;
let pageSize = 10; // Padrão de 10 itens por página
let currentFilterDate = '';
let currentFilterUserId = '';
let currentFilterPaymentMethod = '';

/**
 * Busca e exibe os itens detalhados de uma venda
 */
async function verDetalhesVenda(vendaId) {
    try {
        // Busca itens e dados da venda simultaneamente
        const [itens, venda] = await Promise.all([
            requisitarDados(`/api/itemvendas/venda/${vendaId}`, 'GET'),
            requisitarDados(`/api/vendas/${vendaId}`, 'GET')
        ]);

        const listaItens = document.getElementById('lista-itens-venda');
        const spanId = document.getElementById('detalhe-id-venda');
        const pPagamento = document.getElementById('detalhe-pagamento-venda');
        const pVendedor = document.getElementById('detalhe-vendedor-venda');

        if (spanId) spanId.innerText = vendaId;
        // Alterado de formaPagamento para resumoPagamento para coincidir com o DTO do Backend
        if (pPagamento) pPagamento.innerText = venda.resumoPagamento || venda.formaPagamento || 'Não especificado';
        // Tenta buscar o nome do usuário/vendedor no objeto retornado
        if (pVendedor) pVendedor.innerText = venda.usuario?.nome || venda.user?.nome || 'N/A';

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
        abrirModal('modal-detalhes');
    } catch (error) {
        handleAppError(error);
    }
}

/**
 * Exclui uma venda permanentemente
 */
async function excluirVenda(idVenda) {
    try {
        if (confirm('Tem certeza que deseja excluir esta venda? Esta ação não pode ser desfeita.')) {
            // Assumindo que a exclusão de uma venda deve ser feita no endpoint principal de vendas
            // e que o backend lida com a exclusão em cascata dos itens de venda.
            await requisitarDados(`/api/vendas/${idVenda.trim()}`, 'DELETE');
            alert('Venda excluída com sucesso!');
            await carregarHistoricoVendas(); // Recarrega a lista de vendas após a exclusão
        }
    } catch (error) {
        handleAppError(error);
    }
}


/**
 * Prepara o formulário para edição carregando os dados existentes
 */
async function prepararEdicaoVenda(id) {
    try {
        // 1. Limpeza e preparação inicial do formulário para evitar "sujeira" de vendas anteriores
        // Garante que o formulário é resetado e os pagamentos antigos são removidos
        const form = document.getElementById('form-venda');
        if (form) form.reset();
        const container = document.getElementById('container-pagamentos');
        if (container) container.innerHTML = '';

        // 2. Busca dados da venda, itens e pagamentos simultaneamente
        const [venda, itens, pagamentos] = await Promise.all([
            requisitarDados(`/api/vendas/${id}`, 'GET'),
            requisitarDados(`/api/itemvendas/venda/${id}`, 'GET'),
            requisitarDados(`/api/pagamentos/venda/${id}`, 'GET').catch(() => []) // Fallback caso não exista a rota
        ]);

        // 3. Validação de Prazo de 24 Horas
        const dataVenda = new Date(venda.dataVenda);
        const agora = new Date();
        const diffHoras = (agora - dataVenda) / (1000 * 60 * 60);

        if (diffHoras > 24) {
            alert('Esta venda foi realizada há mais de 24 horas e não pode mais ser editada.');
            return;
        }

        // 4. Carrega os produtos necessários
        await carregarProdutosNoSelect();

        const campoId = document.getElementById('venda-id');
        if (campoId) campoId.value = id;

        // 5. Preenche o produto e a quantidade (assumindo um único item por venda para edição)
        if (itens && itens.length > 0) {
            const item = itens[0]; // Assume lógica de produto único conforme interface atual
            if (document.getElementById('venda-produto')) document.getElementById('venda-produto').value = item.produto?.idProduto || '';
            if (document.getElementById('venda-quantidade')) document.getElementById('venda-quantidade').value = item.quantidadeVendida;
        }

        // 6. Preenche os pagamentos existentes (usando a lista buscada da API)
        if (pagamentos && pagamentos.length > 0) {
            pagamentos.forEach(pagamento => {
                // Passa o objeto de pagamento para a função para preencher os campos
                adicionarLinhaPagamento(pagamento);
            });
        } else {
            // Se não houver pagamentos, adiciona uma linha vazia para começar
            adicionarLinhaPagamento();
        }
        
        // Atualiza o título do modal
        if (document.getElementById('modal-venda-titulo')) {
            document.getElementById('modal-venda-titulo').textContent = "Editar Venda";
        }

        atualizarResumoTotal();
        abrirModal('modal-venda');
    } catch (error) {
        handleAppError(error);
    }
}


/**
 * Salva (Cria ou Atualiza) uma venda.
 * Agora estruturado para criar o ItemPedido relacionando Venda, Produto e Usuário.
 */
async function salvarVenda(event) {
    if (event && event.preventDefault) event.preventDefault();
    
    // Obtém o ID do usuário do Token JWT
    const token = localStorage.getItem('jwt_token');
    const payload = JSON.parse(atob(token.split('.')[1]));
    const usuarioId = payload.id;

    const id = document.getElementById('venda-id')?.value;
    const produtoId = document.getElementById('venda-produto')?.value;
    const quantidade = document.getElementById('venda-quantidade')?.value;
    const paymentRows = document.querySelectorAll('.linha-pagamento');
    
    if (!produtoId || !quantidade || paymentRows.length === 0) {
        alert('Preencha os dados do produto e adicione ao menos um pagamento.');
        return;
    }

    const pagamentos = [];
    let totalPago = 0;

    for (const row of paymentRows) {
        const forma = row.querySelector('.pag-forma').value;
        const valor = parseFloat(row.querySelector('.pag-valor').value);
        const parcelas = parseInt(row.querySelector('.pag-parcelas').value) || 1;

        if (isNaN(valor) || valor <= 0) {
            alert('Informe um valor válido para todos os pagamentos.');
            return;
        }
        
        pagamentos.push({ formaPagamento: forma, valorPago: valor, parcelas: parcelas }); // Corrigido para 'parcelas' para corresponder ao DTO
        totalPago += valor;
    }

    // Garante que há pelo menos um pagamento para extrair a formaPagamento principal
    if (pagamentos.length === 0) {
        alert('Adicione ao menos um pagamento.');
        return;
    }

    const totalEsperado = extrairPrecoDoSelect() * parseInt(quantidade);
    if (Math.abs(totalPago - totalEsperado) > 0.01) {
        alert(`A soma dos pagamentos (R$ ${totalPago.toFixed(2)}) não confere com o total da venda (R$ ${totalEsperado.toFixed(2)}).`);
        return;
    }

    const dados = {
        idUser: parseInt(usuarioId), // Corrigido para 'idUser' para corresponder ao DTO do backend
        formaPagamento: pagamentos[0].formaPagamento,
        dataVenda: new Date().toISOString(),
        valorTotal: totalPago,
        itens: [{ idProduto: parseInt(produtoId), quantidade: parseInt(quantidade) }],
        pagamentos: pagamentos
    };

    const isEdicao = id && id.trim() !== "";
    try {
        await requisitarDados(isEdicao ? `/api/vendas/${id}` : '/api/vendas', isEdicao ? 'PUT' : 'POST', dados);
        alert(isEdicao ? 'Venda atualizada!' : 'Venda realizada!');
        fecharModal('modal-venda');
        await carregarHistoricoVendas();
    } catch (error) {
        handleAppError(error);
    }
}

/**
 * Calcula e atualiza visualmente a soma dos pagamentos informados
 */
function atualizarTotalPago() {
    const rows = document.querySelectorAll('.linha-pagamento');
    let soma = 0;
    rows.forEach(row => {
        const valor = parseFloat(row.querySelector('.pag-valor').value) || 0;
        soma += valor;
    });

    const spanPago = document.getElementById('valor-total-pago');
    const totalVendaText = document.getElementById('valor-total-venda').innerText;
    const totalVenda = parseFloat(totalVendaText.replace(/[^\d,.-]/g, '').replace(',', '.')) || 0;

    if (spanPago) {
        spanPago.innerText = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(soma);
        
        // Validação visual de correspondência de valores
        spanPago.classList.remove('status-match', 'status-mismatch');
        const diff = Math.abs(soma - totalVenda);
        spanPago.classList.add(diff < 0.01 ? 'status-match' : 'status-mismatch');
    }
}

/**
 * Adiciona uma nova linha de pagamento dinamicamente
 */
function adicionarLinhaPagamento(pagamento = null) { // Aceita um objeto de pagamento opcional
    const container = document.getElementById('container-pagamentos');
    if (!container) {
        console.error("Erro: O elemento 'container-pagamentos' não foi encontrado no HTML.");
        return;
    }

    const div = document.createElement('div');
    div.className = 'linha-pagamento mb-2 d-flex gap-1 align-items-center';
    div.innerHTML = `
        <select class="pag-forma" onchange="window.toggleParcelas(this)" required>
            <option value="DINHEIRO">Dinheiro</option>
            <option value="PIX">Pix</option>
            <option value="CARTAO_DEBITO">Débito</option>
            <option value="CARTAO_CREDITO">Crédito</option>
        </select>
        <input type="number" class="pag-valor" placeholder="Valor R$" step="0.01" required style="flex: 1;" oninput="window.atualizarTotalPago()">
        <select class="pag-parcelas" style="display: none;">
            <option value="1">1x</option>
            <option value="2">2x</option>
            <option value="3">3x</option>
            <option value="4">4x</option>
        </select>
        <button type="button" class="btn btn-danger btn-table-action" onclick="this.parentElement.remove(); window.atualizarTotalPago();">×</button>
    `;
    container.appendChild(div);

    // Preenche os valores se um objeto de pagamento for fornecido
    if (pagamento) {
        const selectForma = div.querySelector('.pag-forma');
        const inputValor = div.querySelector('.pag-valor');
        const selectParcelas = div.querySelector('.pag-parcelas');

        if (selectForma) selectForma.value = pagamento.formaPagamento || 'DINHEIRO';
        if (inputValor) inputValor.value = pagamento.valorPago || '';
        if (selectParcelas) selectParcelas.value = pagamento.parcelas || '1';
        window.toggleParcelas(selectForma); // Garante que o select de parcelas é exibido/ocultado corretamente
    }
}

/**
 * Mostra/esconde o select de parcelas dependendo da forma de pagamento
 */
function toggleParcelas(select) {
    const row = select.parentElement;
    const selectParcelas = row.querySelector('.pag-parcelas');
    selectParcelas.style.display = select.value === 'CARTAO_CREDITO' ? 'block' : 'none';
    if (select.value !== 'CARTAO_CREDITO') selectParcelas.value = "1";
}

/**
 * Atualiza o label do valor total no modal
 */
function atualizarResumoTotal() {
    const qty = parseInt(document.getElementById('venda-quantidade')?.value) || 0;
    const preco = extrairPrecoDoSelect();
    const total = qty * preco;
    const span = document.getElementById('valor-total-venda');
    if (span) span.innerText = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(total);
    atualizarTotalPago(); // Revalida se o total pago ainda bate com o novo total da venda
}

function extrairPrecoDoSelect() {
    const select = document.getElementById('venda-produto');
    if (!select || select.selectedIndex <= 0) return 0;
    const match = select.options[select.selectedIndex].text.match(/R\$\s?([\d,.]+)/);
    return match ? parseFloat(match[1].replace(',', '.')) : 0;
}

/**
 * Busca os produtos da API e preenche o select no modal
 */
async function carregarProdutosNoSelect() {
    try {
        // Agora buscamos do estoque para garantir que listamos o que realmente está na adega
        const estoques = await requisitarDados('/api/estoques', 'GET');
        const select = document.getElementById('venda-produto');
        if (select) {
            select.innerHTML = '<option value="">Selecione um produto...</option>';
            
            // Filtramos apenas itens que possuem quantidade disponível
            estoques.filter(item => item.quantidade > 0).forEach(item => {
                const p = item.produto;
                const opt = document.createElement('option');
                opt.value = p.idProduto;
                // Exibimos a quantidade disponível para auxiliar o vendedor
                opt.textContent = `${p.nomeProduto} - R$ ${p.valorUnitario.toFixed(2)} (${item.quantidade} un)`;
                select.appendChild(opt);
            });
        }
    } catch (error) {
        console.error("Erro ao carregar produtos:", error);
    }
}

async function iniciarNovaVenda() { // Make it async
    const form = document.getElementById('form-venda');
    if (form) {
        form.reset();
    }
    if (document.getElementById('venda-id')) document.getElementById('venda-id').value = '';
    if (document.getElementById('modal-venda-titulo')) {
        document.getElementById('modal-venda-titulo').textContent = "Nova Venda";
    }
    const container = document.getElementById('container-pagamentos');
    if (container) container.innerHTML = '';
    
    await carregarProdutosNoSelect();
    adicionarLinhaPagamento(); // Inicia com uma linha de pagamento padrão
    atualizarResumoTotal();
    atualizarTotalPago();
    abrirModal('modal-venda');
}

/**
 * Carrega o histórico de vendas com paginação.
 * @param {number} page - O número da página a ser carregada (0-indexed).
 * @param {number} size - O número de itens por página.
 */
async function carregarHistoricoVendas(page = currentPage, size = pageSize, filterDate = currentFilterDate, filterUserId = currentFilterUserId, filterPaymentMethod = currentFilterPaymentMethod) {
    // Se disparado por evento (ex: DOMContentLoaded), 'page' será um objeto Event. 
    // Precisamos garantir que usamos os valores numéricos.
    const pageToLoad = (typeof page === 'number') ? page : currentPage;
    const sizeToLoad = (typeof size === 'number') ? size : pageSize;

    // Atualiza as variáveis globais de filtro
    currentFilterDate = filterDate;
    currentFilterUserId = filterUserId;
    currentFilterPaymentMethod = filterPaymentMethod;

    let apiUrl = `/api/vendas?page=${pageToLoad}&size=${sizeToLoad}&sort=idVenda,desc`;

    // Constrói a URL com base nos filtros ativos
    // Prioriza data, depois usuário, depois forma de pagamento.
    // Para filtros combinados, o backend e esta lógica precisariam ser mais complexos.
    if (currentFilterDate) {
        apiUrl = `/api/vendas/por-data?data=${currentFilterDate}&page=${pageToLoad}&size=${sizeToLoad}&sort=idVenda,desc`;
    } else if (currentFilterUserId) {
        apiUrl = `/api/vendas/por-usuario?idUsuario=${currentFilterUserId}&page=${pageToLoad}&size=${sizeToLoad}&sort=idVenda,desc`;
    } else if (currentFilterPaymentMethod) {
        apiUrl = `/api/vendas/por-forma-pagamento?formaPagamento=${currentFilterPaymentMethod}&page=${pageToLoad}&size=${sizeToLoad}&sort=idVenda,desc`;
    }

    try {
        // Fazemos a requisição.
        // Se a API retornar uma lista simples (sem paginação), totalPages será 1 e content será o próprio array.
        // Se a API retornar um objeto Page, os dados estarão em .content.
        const response = await requisitarDados(apiUrl, 'GET');
        
        // Spring Data Page retorna os dados em .content. Caso contrário, assume-se que é o array direto.
        const vendas = response && response.content ? response.content : (Array.isArray(response) ? response : []);
        
        const totalPages = response.totalPages !== undefined ? response.totalPages : 1;
        const totalElements = response.totalElements !== undefined ? response.totalElements : vendas.length;

        const tbody = document.getElementById('tabela-vendas-body');
        if (!tbody) {
            console.error("Elemento 'tabela-vendas-body' não encontrado.");
            return;
        }

        tbody.innerHTML = ''; // Limpa a tabela antes de preencher

        if (vendas && vendas.length > 0) {
            const moneyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
            const dateFormatter = new Intl.DateTimeFormat('pt-BR', {
                year: 'numeric', month: '2-digit', day: '2-digit',
                hour: '2-digit', minute: '2-digit'
            });

            vendas.forEach(venda => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${venda.idVenda}</td>
                    <td>${dateFormatter.format(new Date(venda.dataVenda))}</td>
                    <td>${venda.resumoPagamento || venda.formaPagamento || '---'}</td>
                    <td>${moneyFormatter.format(venda.valorTotal)}</td>
                    <td>
                        <span class="badge ${venda.active ? 'status-ok' : 'status-danger'}">
                            ${venda.active ? 'Ativa' : 'Cancelada'}
                        </span>
                    </td>
                    <td class="nowrap">
                        <button class="btn btn-info btn-table-action" onclick="window.verDetalhesVenda(${venda.idVenda})">Detalhes</button>
                        <button class="btn btn-warning btn-table-action" onclick="window.prepararEdicaoVenda(${venda.idVenda})">Editar</button>
                        <button class="btn btn-danger btn-table-action" onclick="window.excluirVenda(${venda.idVenda})">Excluir</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Nenhuma venda encontrada.</td></tr>';
        }

        // Atualiza o estado da paginação
        currentPage = pageToLoad;
        pageSize = sizeToLoad;
        updatePaginationControls(totalPages, totalElements);

    } catch (error) {
        handleAppError(error);
        const tbody = document.getElementById('tabela-vendas-body');
        if (tbody) tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Erro ao carregar dados do servidor.</td></tr>';
    }
}

function updatePaginationControls(totalPages, totalElements) {
    const paginationInfo = document.getElementById('pagination-info');
    const prevBtn = document.getElementById('prev-page-btn');
    const nextBtn = document.getElementById('next-page-btn');

    if (paginationInfo) paginationInfo.innerText = `Página ${currentPage + 1} de ${totalPages} (${totalElements} vendas)`;
    if (prevBtn) prevBtn.disabled = currentPage === 0;
    if (nextBtn) nextBtn.disabled = currentPage >= totalPages - 1;
}

// Nova função para aplicar os filtros
function aplicarFiltrosVendas() {
    const filterDateInput = document.getElementById('filter-data-venda'); // Assumindo que este ID existe no HTML
    const filterUserIdInput = document.getElementById('filter-usuario-id'); // Assumindo que este ID existe no HTML
    const filterPaymentMethodSelect = document.getElementById('filter-forma-pagamento'); // Assumindo que este ID existe no HTML

    const date = filterDateInput ? filterDateInput.value : '';
    const userId = filterUserIdInput ? filterUserIdInput.value : '';
    const paymentMethod = filterPaymentMethodSelect ? filterPaymentMethodSelect.value : '';

    // Reseta para a primeira página ao aplicar novos filtros
    currentPage = 0;
    carregarHistoricoVendas(currentPage, pageSize, date, userId, paymentMethod);
}

// Função para limpar filtros
function limparFiltrosVendas() {
    const filterDateInput = document.getElementById('filter-data-venda');
    const filterUserIdInput = document.getElementById('filter-usuario-id');
    const filterPaymentMethodSelect = document.getElementById('filter-forma-pagamento');

    if (filterDateInput) filterDateInput.value = '';
    if (filterUserIdInput) filterUserIdInput.value = '';
    if (filterPaymentMethodSelect) filterPaymentMethodSelect.value = '';

    currentFilterDate = '';
    currentFilterUserId = '';
    currentFilterPaymentMethod = '';
    currentPage = 0;
    carregarHistoricoVendas(currentPage, pageSize);
}

// Expose functions to the global scope for onclick attributes in HTML
window.verDetalhesVenda = verDetalhesVenda;
window.prepararEdicaoVenda = prepararEdicaoVenda;
window.excluirVenda = excluirVenda;
window.adicionarLinhaPagamento = adicionarLinhaPagamento;
window.toggleParcelas = toggleParcelas;
window.atualizarTotalPago = atualizarTotalPago;
window.atualizarResumoTotal = atualizarResumoTotal;
window.salvarVenda = salvarVenda;
window.aplicarFiltrosVendas = aplicarFiltrosVendas; // Expor a nova função de filtro
window.limparFiltrosVendas = limparFiltrosVendas;
window.iniciarNovaVenda = iniciarNovaVenda;

// Funções de paginação para serem chamadas do HTML
const goToPage = (pageNumberOrEvent) => {
    let pageToLoad;
    if (typeof pageNumberOrEvent === 'number') {
        pageToLoad = pageNumberOrEvent;
    } else if (pageNumberOrEvent && pageNumberOrEvent.target) {
        // Tenta extrair de um atributo data-page se for um evento
        pageToLoad = parseInt(pageNumberOrEvent.target.dataset.page) || 0;
    } else {
        pageToLoad = currentPage;
    }
    
    currentPage = pageToLoad;
    carregarHistoricoVendas(currentPage, pageSize, currentFilterDate, currentFilterUserId, currentFilterPaymentMethod);
};

const changePageSize = (selectElement) => {
    // Garante que selectElement é um elemento HTML e tem um valor numérico.
    // Se por algum motivo 'selectElement' não for um objeto com 'value', ou 'value' não for numérico,
    // ele irá default para o pageSize atual.
    const newSize = selectElement && selectElement.value ? parseInt(selectElement.value) : pageSize;
    pageSize = newSize;
    currentPage = 0; // Reseta para a primeira página ao mudar o tamanho
    carregarHistoricoVendas(currentPage, pageSize, currentFilterDate, currentFilterUserId, currentFilterPaymentMethod); // Chama com os valores numéricos
};

const nextPage = () => {
    currentPage++; // currentPage já é um número, então a operação é segura.
    carregarHistoricoVendas(currentPage, pageSize, currentFilterDate, currentFilterUserId, currentFilterPaymentMethod);
};

const prevPage = () => {
    currentPage--;
    carregarHistoricoVendas(currentPage, pageSize, currentFilterDate, currentFilterUserId, currentFilterPaymentMethod);
};

window.goToPage = goToPage;
window.changePageSize = changePageSize;
window.nextPage = nextPage;
window.prevPage = prevPage;

export {
    verDetalhesVenda,
    excluirVenda,
    prepararEdicaoVenda,
    salvarVenda,
    atualizarTotalPago,
    adicionarLinhaPagamento,
    toggleParcelas,
    atualizarResumoTotal,
    iniciarNovaVenda,
    carregarHistoricoVendas,
    aplicarFiltrosVendas,
    limparFiltrosVendas,
    goToPage,
    changePageSize,
    nextPage,
    prevPage
}