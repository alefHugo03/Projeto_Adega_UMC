import requisitarDados from '../conection/query.js';
import { abrirModal, fecharModal } from '../modules/modal.js';
import { isAdmin } from '../usuario/acoes.js';
import { handleAppError } from '../exception/exceptions.js';

// Carrega o estoque do backend e renderiza a tabela de estoque na página.
async function carregarEstoque() {
    try {
        const estoques = await requisitarDados('/api/estoques', 'GET');
        const tbody = document.getElementById('tabela-estoque-body');
        if (!tbody) return; 

        tbody.innerHTML = (estoques && estoques.length > 0) ? '' : '<tr><td colspan="5" class="empty-state">Nenhum estoque registrado.</td></tr>';

        estoques.forEach(item => {
            const tr = document.createElement('tr');
            const statusClass = item.quantidade >= 10 ? 'status-ok' : (item.quantidade >= 5 ? 'status-warning' : 'status-danger');
            const statusTexto = item.quantidade >= 10 ? 'OK' : (item.quantidade >= 5 ? 'Baixo' : 'Crítico');

            const tdProduto = document.createElement('td');
            tdProduto.textContent = item.produto.nomeProduto;

            const tdTipo = document.createElement('td');
            tdTipo.textContent = item.produto.tipoProduto;

            const tdQuantidade = document.createElement('td');
            tdQuantidade.innerHTML = `<strong>${item.quantidade}</strong>`;

            const tdStatus = document.createElement('td');
            tdStatus.innerHTML = `<span class="badge ${statusClass}">${statusTexto}</span>`;

            const tdAcoes = document.createElement('td');

            const btnAtualizar = document.createElement('button');
            btnAtualizar.className = 'btn btn-warning btn-table-action';
            btnAtualizar.textContent = 'Atualizar';
            btnAtualizar.addEventListener('click', () => prepararEntradaEstoque(item.idEstoque, item.produto.idProduto, item.produto.nomeProduto, item.quantidade));
            tdAcoes.appendChild(btnAtualizar);

            if (isAdmin()) {
                const btnRetiradaElemento = document.createElement('button');
                btnRetiradaElemento.className = 'btn btn-danger btn-table-action';
                btnRetiradaElemento.textContent = 'Baixa';
                btnRetiradaElemento.addEventListener('click', () => window.prepararRetirada(item.produto.idProduto, item.produto.nomeProduto));
                tdAcoes.appendChild(btnRetiradaElemento);
            }

            tr.appendChild(tdProduto);
            tr.appendChild(tdTipo);
            tr.appendChild(tdQuantidade);
            tr.appendChild(tdStatus);
            tr.appendChild(tdAcoes);
            tbody.appendChild(tr);
        });
    } catch (error) {
        handleAppError(error);
        const tbody = document.getElementById('tabela-estoque-body');
        if (tbody) tbody.innerHTML = '<tr><td colspan="5" class="empty-state">Erro ao carregar dados do estoque.</td></tr>';
    }
}

// Prepara o modal para criar uma nova entrada de estoque (reseta formulário e carrega produtos).
async function iniciarNovaEntrada() {
    document.getElementById('form-estoque').reset();
    document.getElementById('estoque-id').value = '';
    
    // Alterna visibilidade para permitir seleção de produto
    document.getElementById('estoque-produto-id').value = '';
    document.getElementById('estoque-produto-selecao').style.display = 'block';
    document.getElementById('estoque-produto-nome-fixo').style.display = 'none';

    await carregarProdutosNoSelect();
    abrirModal('modal-estoque');
}

// Prepara o modal para editar uma entrada de estoque existente (preenche campos).
function prepararEntradaEstoque(idEstoque, idProduto, nomeProduto, qtdAtual) {
    document.getElementById('estoque-id').value = idEstoque || '';
    document.getElementById('estoque-produto-id').value = idProduto;
    
    const displayNome = document.getElementById('estoque-produto-nome-fixo');
    displayNome.value = nomeProduto;
    displayNome.style.display = 'block';
    document.getElementById('estoque-produto-selecao').style.display = 'none';

    document.getElementById('estoque-quantidade').value = '';
    abrirModal('modal-estoque');
}

// Busca produtos do backend e popula o select do modal de entrada.
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

// Envia a entrada de estoque para a API (cria ou atualiza), usando os campos do formulário.
async function salvarEstoque(event) {
    event.preventDefault();
    const idProdutoFixo = document.getElementById('estoque-produto-id').value;
    const idProdutoSelect = document.getElementById('estoque-produto-selecao').value;
    const idProduto = idProdutoFixo || idProdutoSelect;
    const quantidadeAdicional = parseInt(document.getElementById('estoque-quantidade').value);
    const idEstoque = document.getElementById('estoque-id').value;

    if (!idProduto || isNaN(quantidadeAdicional)) {
        alert('Por favor, selecione um produto e informe uma quantidade válida.');
        return;
    }

    const dados = {
        idEstoque: idEstoque ? parseInt(idEstoque) : null, // Incluído caso o backend precise para identificar o registro
        quantidade: quantidadeAdicional,
        produto: { idProduto: parseInt(idProduto) }
    };

    try {
        await requisitarDados('/api/estoques', 'POST', dados); 
        alert('Entrada de estoque realizada com sucesso!');
        fecharModal('modal-estoque');
        carregarEstoque();
    } catch (error) {
        handleAppError(error);
    }
}

// Fluxo de retirada administrativa (rodado por admin): solicita quantidades/motivo e registra uma venda de baixa.
window.prepararRetirada = async (idProduto, nome) => {
    if (!isAdmin()) {
        alert("Acesso negado: Somente administradores podem realizar retiradas administrativas.");
        return;
    }

    const qtd = prompt(`[RETIRADA ADMINISTRATIVA]\nProduto: ${nome}\n\nInforme a quantidade para retirar do estoque (será registrado como despesa):`);
    
    if (!qtd || isNaN(qtd) || parseInt(qtd) <= 0) return;

    const motivo = prompt("Informe o motivo da retirada (Ex: Quebra, Degustação, Brinde):");
    if (!motivo) return;

    const token = localStorage.getItem('jwt_token');
    if (!token) {
        alert("Usuário não autenticado. Por favor, faça login novamente.");
        return;
    }
    
    const payload = JSON.parse(atob(token.split('.')[1]));

    const dados = {
        idUser: payload.id,
        formaPagamento: 'RETIRADA_ADMIN',
        valorTotal: 0,
        motivo: motivo,
        itens: [{ idProduto: parseInt(idProduto), quantidade: parseInt(qtd) }],
        pagamentos: [{ formaPagamento: 'RETIRADA_ADMIN', valorPago: 0, parcelas: 1 }]
    };

    try {
        await requisitarDados('/api/vendas', 'POST', dados);
        alert('Baixa de estoque registrada com sucesso!');
        await carregarEstoque();
    } catch (error) {
        handleAppError(error);
    }
};

export { carregarEstoque,iniciarNovaEntrada,prepararEntradaEstoque,carregarProdutosNoSelect,salvarEstoque }
