import requisitarDados from '../conection/query.js';
import { carregarHistoricoVendas } from './historico.js';
import { abrirModal, fecharModal } from '../modules/modal.js';
import excluir from '../conection/excluir.js';

/**
 * Busca e exibe os itens detalhados de uma venda
 */
export async function verDetalhesVenda(vendaId) {
    try {
        // Busca itens e dados da venda simultaneamente
        const [itens, venda] = await Promise.all([
            requisitarDados(`/api/itemvendas/venda/${vendaId}`, 'GET'),
            requisitarDados(`/api/vendas/${vendaId}`, 'GET')
        ]);

        const listaItens = document.getElementById('lista-itens-venda');
        const spanId = document.getElementById('detalhe-id-venda');
        const pPagamento = document.getElementById('detalhe-pagamento-venda');

        if (spanId) spanId.innerText = vendaId;
        if (pPagamento) pPagamento.innerText = venda.resumoPagamento || 'Não especificado';

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
        console.error('Erro ao buscar itens:', error);
        alert('Erro ao carregar detalhes da venda.');
    }
}

/**
 * Exclui uma venda permanentemente
 */
export async function excluirVenda(idVenda) {
    // Agora usamos a função genérica passando o callback de atualização
    return excluir(`/api/itemvendas/venda/${idVenda.trim()}`, 'Venda', carregarHistoricoVendas);
}


/**
 * Prepara o formulário para edição carregando os dados existentes
 */
export async function prepararEdicaoVenda(id) {
    try {
        const venda = await requisitarDados(`/api/vendas/${id}`, 'GET');
        
        const campoId = document.getElementById('venda-id');
        const tituloModal = document.getElementById('modal-venda-titulo');

        if (campoId) campoId.value = venda.idVenda || venda.id || id;
        
        // Nota: Para edição com múltiplos pagamentos, você precisará iterar 
        // sobre venda.pagamentos e chamar adicionarLinhaPagamento() para cada um.
        
        if (tituloModal) tituloModal.textContent = "Editar Venda";
        
        abrirModal('modal-venda');
    } catch (error) {
        console.error('Erro ao buscar dados da venda:', error);
        alert('Erro ao carregar dados para edição.');
    }
}


/**
 * Salva (Cria ou Atualiza) uma venda
 * Agora estruturado para criar o ItemPedido relacionando Venda, Produto e Usuário.
 */
export async function salvarVenda(event) {
    if (event && event.preventDefault) event.preventDefault();
    
    const id = document.getElementById('venda-id')?.value;
    const usuarioId = document.getElementById('venda-usuario')?.value;
    const produtoId = document.getElementById('venda-produto')?.value;
    const quantidade = document.getElementById('venda-quantidade')?.value;
    const paymentRows = document.querySelectorAll('.linha-pagamento');

    if (!usuarioId || !produtoId || !quantidade || paymentRows.length === 0) {
        alert('Preencha todos os dados e adicione ao menos um pagamento.');
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
        formaPagamento: pagamentos[0].formaPagamento, // Adicionado para satisfazer a validação do DTO
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
        alert('Erro ao processar venda: ' + error.message);
    }
}

/**
 * Calcula e atualiza visualmente a soma dos pagamentos informados
 */
export function atualizarTotalPago() {
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
export function adicionarLinhaPagamento() {
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
}

/**
 * Mostra/esconde o select de parcelas dependendo da forma de pagamento
 */
export function toggleParcelas(select) {
    const row = select.parentElement;
    const selectParcelas = row.querySelector('.pag-parcelas');
    selectParcelas.style.display = select.value === 'CARTAO_CREDITO' ? 'block' : 'none';
    if (select.value !== 'CARTAO_CREDITO') selectParcelas.value = "1";
}

/**
 * Atualiza o label do valor total no modal
 */
export function atualizarResumoTotal() {
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
        const produtos = await requisitarDados('/api/produtos', 'GET');
        const select = document.getElementById('venda-produto');
        if (select) {
            select.innerHTML = '<option value="">Selecione um produto...</option>';
            produtos.forEach(p => {
                const opt = document.createElement('option');
                opt.value = p.idProduto;
                opt.textContent = `${p.nomeProduto} - R$ ${p.valorUnitario.toFixed(2)}`;
                select.appendChild(opt);
            });
        }
    } catch (error) {
        console.error("Erro ao carregar produtos:", error);
    }
}

/**
 * Busca os usuários da API e preenche o select no modal
 */
async function carregarUsuariosNoSelect() {
    try {
        const usuarios = await requisitarDados('/api/usuarios', 'GET');
        const select = document.getElementById('venda-usuario');
        if (select) {
            select.innerHTML = '<option value="">Selecione um usuário...</option>';
            usuarios.forEach(u => {
                const opt = document.createElement('option');
                opt.value = u.id;
                opt.textContent = u.nome;
                select.appendChild(opt);
            });
        }
    } catch (error) {
        console.error("Erro ao carregar usuários:", error);
    }
}


export async function iniciarNovaVenda() { // Make it async
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
    await carregarUsuariosNoSelect();
    adicionarLinhaPagamento(); // Inicia com uma linha de pagamento padrão
    atualizarResumoTotal();
    atualizarTotalPago();
    abrirModal('modal-venda');
}