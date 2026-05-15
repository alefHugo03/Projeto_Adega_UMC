import requisitarDados from '../conection/query.js';

/**
 * Configurações globais de cores para combinar com o tema Black/Gold
 */
const colors = {
    primary: '#D4AF37',
    hover: '#B8860B',
    text: '#F5F5F5',
    grid: '#333',
    palette: ['#D4AF37', '#8B4513', '#DAA520', '#B8860B', '#F5DEB3', '#A0522D']
};

const chartExplanations = {
    estoque: "Este gráfico exibe a quantidade atual de cada produto em sua adega, auxiliando no controle de reposição.",
    categoria: "Mostra como seus produtos estão distribuídos entre as categorias cadastradas (Vinhos, Cervejas, etc).",
    pagamento: "Analisa a preferência dos clientes em relação às formas de pagamento utilizadas nas vendas ativas.",
    vendedor: "Ranking de desempenho financeiro dos vendedores, baseado no valor total bruto de vendas realizadas.",
    vendasTempo: "Acompanha o faturamento total diário nos últimos 7 dias, ideal para identificar tendências semanais."
};

let currentChart = null; // Variável para armazenar a instância do gráfico atual
let dashboardData = { estoques: [], vendas: [] }; // Armazena os dados carregados

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const [estoques, responseVendas] = await Promise.all([ // Carrega todos os dados de uma vez
            requisitarDados('/api/estoques', 'GET'),
            requisitarDados('/api/vendas?size=1000', 'GET') // Aumentado para pegar todos os dados para o gráfico
        ]);

        // Como o backend agora é paginado, extraímos o conteúdo de .content
        const vendas = responseVendas && responseVendas.content ? responseVendas.content : (Array.isArray(responseVendas) ? responseVendas : []);
        dashboardData = { estoques, vendas }; 

        processarEstatisticas(estoques, vendas);

        const chartSelector = document.getElementById('chart-selector');
        if (chartSelector) {
            chartSelector.addEventListener('change', (event) => {
                renderSelectedChart(event.target.value);
            });
            // Renderiza o gráfico padrão ao carregar a página
            renderSelectedChart(chartSelector.value);
        }
    } catch (error) {
        console.error("Erro ao carregar dados do dashboard:", error);
    }
});

function renderSelectedChart(chartType) {
    const chartTitle = document.getElementById('chart-title');
    const chartDesc = document.getElementById('chart-description');

    if (currentChart) {
        currentChart.destroy(); // Destroi o gráfico anterior para evitar sobreposição
    }

    if (chartDesc) chartDesc.textContent = chartExplanations[chartType] || "";

    const ctx = document.getElementById('mainChart').getContext('2d');

    switch (chartType) {
        case 'estoque': chartTitle.textContent = 'Estoque por Produto'; currentChart = renderEstoqueChart(ctx, dashboardData.estoques); break;
        case 'categoria': chartTitle.textContent = 'Distribuição por Categoria'; currentChart = renderCategoriaChart(ctx, dashboardData.estoques); break;
        case 'pagamento': chartTitle.textContent = 'Formas de Pagamento'; currentChart = renderPagamentoChart(ctx, dashboardData.vendas); break;
        case 'vendedor': chartTitle.textContent = 'Vendas por Vendedor (R$)'; currentChart = renderVendedorChart(ctx, dashboardData.vendas); break;
        case 'vendasTempo': chartTitle.textContent = 'Evolução de Vendas (Últimos 7 dias)'; currentChart = renderVendasTempoChart(ctx, dashboardData.vendas); break;
        default: chartTitle.textContent = 'Selecione um Gráfico'; break;
    }
}

/** Calcula e exibe as estatísticas nos cards inferiores */
function processarEstatisticas(estoques, vendas) {
    const moneyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

    // 1. Estoque Crítico
    const criticoEl = document.getElementById('stats-estoque-critico');
    const itensCriticos = estoques.filter(e => e.quantidade < 10);
    if (criticoEl) {
        criticoEl.innerHTML = itensCriticos.length > 0 
            ? itensCriticos.slice(0, 3).map(e => `<p class="mb-1">⚠️ ${e.produto.nomeProduto} (${e.quantidade} un)</p>`).join('')
            : '<p class="text-success">✅ Tudo sob controle</p>';
    }

    // 2. Melhor Vendedor
    const vendedores = {};
    vendas.filter(v => v.active).forEach(v => {
        const nome = v.usuario?.nome || v.user?.nome || 'Sistema';
        vendedores[nome] = (vendedores[nome] || 0) + (v.valorTotal || 0);
    });
    
    let melhorVendedor = "Nenhum";
    let maiorValor = 0;
    for (const [nome, valor] of Object.entries(vendedores)) {
        if (valor > maiorValor) { maiorValor = valor; melhorVendedor = nome; }
    }
    document.getElementById('stats-melhor-vendedor').textContent = melhorVendedor;
    document.getElementById('stats-vendedor-valor').textContent = moneyFormatter.format(maiorValor);

    // 3. Ticket Médio
    const vendasAtivas = vendas.filter(v => v.active);
    const totalReceita = vendasAtivas.reduce((acc, v) => acc + (v.valorTotal || 0), 0);
    const ticketMedio = vendasAtivas.length > 0 ? totalReceita / vendasAtivas.length : 0;
    document.getElementById('stats-ticket-medio').textContent = moneyFormatter.format(ticketMedio);

    // 4. Valor Total em Estoque
    const valorEstoque = estoques.reduce((acc, e) => acc + (e.quantidade * e.produto.valorUnitario), 0);
    document.getElementById('stats-valor-estoque').textContent = moneyFormatter.format(valorEstoque);
}

/** 1. Gráfico de Estoque por Produto (Barra Vertical) */
function renderEstoqueChart(ctx, dados) {
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: dados.map(d => d.produto.nomeProduto),
            datasets: [{
                label: 'Qtd em Estoque',
                data: dados.map(d => d.quantidade),
                backgroundColor: colors.primary,
                borderColor: colors.primary,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, grid: { color: colors.grid }, ticks: { color: colors.text } },
                x: { ticks: { color: colors.text } }
            }
        }
    });
}

/** 2. Distribuição por Categoria (Doughnut) */
function renderCategoriaChart(ctx, dados) {
    const categorias = {};
    dados.forEach(d => {
        const cat = d.produto.tipoProduto;
        categorias[cat] = (categorias[cat] || 0) + 1;
    });

    return new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(categorias),
            datasets: [{
                data: Object.values(categorias),
                backgroundColor: colors.palette,
                borderWidth: 0
            }]
        },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom', labels: { color: colors.text } } } }
    });
}

/** 3. Formas de Pagamento (Pie) */
function renderPagamentoChart(ctx, vendas) {
    const pagamentos = {};
    vendas.filter(v => v.active).forEach(v => {
        // Tenta resumoPagamento, cai para formaPagamento, senão 'Outros'
        const forma = v.resumoPagamento || v.formaPagamento || 'Outros';
        pagamentos[forma] = (pagamentos[forma] || 0) + 1;
    });

    return new Chart(ctx, {
        type: 'pie',
        data: {
            labels: Object.keys(pagamentos),
            datasets: [{
                data: Object.values(pagamentos),
                backgroundColor: ['#28a745', '#007bff', '#ffc107', '#D4AF37'],
                borderWidth: 0
            }]
        },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom', labels: { color: colors.text } } } }
    });
}

/** 4. Desempenho por Vendedor (Barra Horizontal) */
function renderVendedorChart(ctx, vendas) {
    const vendedores = {};
    vendas.filter(v => v.active).forEach(v => {
        // Correção: Verifica tanto 'usuario' quanto 'user' e garante que o valor seja numérico
        const nome = v.usuario?.nome || v.user?.nome || 'Sistema';
        const valor = parseFloat(v.valorTotal) || 0;
        vendedores[nome] = (vendedores[nome] || 0) + valor;
    });

    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(vendedores),
            datasets: [{
                label: 'Total Vendido (R$)',
                data: Object.values(vendedores),
                backgroundColor: '#DAA520'
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            scales: { x: { ticks: { color: colors.text }, grid: { color: colors.grid } }, y: { ticks: { color: colors.text } } }
        }
    });
}

/** 5. Vendas no Tempo - Últimos 7 dias (Line) */
function renderVendasTempoChart(ctx, vendas) {
    const ultimos7Dias = {};
    for (let i = 6; i >= 0; i--) {
        const d = new Date();
        d.setDate(d.getDate() - i);
        ultimos7Dias[d.toLocaleDateString('pt-BR').slice(0, 5)] = 0;
    }

    vendas.filter(v => v.active).forEach(v => {
        const data = new Date(v.dataVenda).toLocaleDateString('pt-BR').slice(0, 5);
        const valor = parseFloat(v.valorTotal) || 0;
        if (ultimos7Dias[data] !== undefined) ultimos7Dias[data] += valor;
    });

    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: Object.keys(ultimos7Dias),
            datasets: [{
                label: 'Vendas Diárias (R$)',
                data: Object.values(ultimos7Dias),
                borderColor: colors.primary,
                backgroundColor: 'rgba(212, 175, 55, 0.1)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: { y: { ticks: { color: colors.text }, grid: { color: colors.grid } }, x: { ticks: { color: colors.text } } }
        }
    });
}