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

const chartDefaults = {
    responsive: true,
    maintainAspectRatio: false,
    animation: { duration: 900, easing: 'easeOutQuart' }
};

const currencyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const shortDateFormatter = new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: '2-digit' });

// Variáveis para armazenar as instâncias dos gráficos
let instanceSemanal = null;
let instanceMensal = null;
let instanceEstoque = null;
let dashboardData = { estoques: [], vendas: [] }; // Armazena os dados carregados

/* 
*   Processamento dos dados 
*/
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const [estoques, responseVendas] = await Promise.all([ 
            requisitarDados('/api/estoques', 'GET'),
            requisitarDados('/api/vendas?size=1000', 'GET')
        ]);

        const vendas = responseVendas && responseVendas.content ? responseVendas.content : (Array.isArray(responseVendas) ? responseVendas : []);
        dashboardData = { estoques, vendas }; 

        processarEstatisticas(estoques, vendas);

        // Listeners para os seletores
        document.getElementById('selector-semanal')?.addEventListener('change', (e) => atualizarGraficoSemanal(e.target.value));
        document.getElementById('selector-mensal')?.addEventListener('change', (e) => atualizarGraficoMensal(e.target.value));

        // Inicializa os gráficos padrões
        atualizarGraficoSemanal('vendasTempo');
        atualizarGraficoMensal('vendasMensal');
        
        // Gráfico de estoque é fixo agora
        const ctxEstoque = document.getElementById('chartEstoque')?.getContext('2d');
        if (ctxEstoque) instanceEstoque = renderEstoqueChart(ctxEstoque, estoques);

        renderQuickLists(estoques, vendas);
    } catch (error) {
        console.error("Erro ao carregar dados do dashboard:", error);
    }
});

/** Atualiza o gráfico da div Semanal */
function atualizarGraficoSemanal(type) {
    if (instanceSemanal) instanceSemanal.destroy();
    const ctx = document.getElementById('chartSemanal').getContext('2d');
    const hoje = new Date();
    const inicioSemana = new Date(hoje);
    inicioSemana.setDate(hoje.getDate() - hoje.getDay());
    inicioSemana.setHours(0,0,0,0);
    // Filtra vendas estritamente desta semana
    const vendasSemana = dashboardData.vendas.filter(v => {
        const d = new Date(v.dataVenda);
        return d >= inicioSemana && d <= hoje;
    });

    switch (type) {
        case 'vendasTempo': instanceSemanal = renderVendasTempoChart(ctx, vendasSemana); break;
        case 'pagamento': instanceSemanal = renderPagamentoChart(ctx, vendasSemana); break;
        case 'vendedor': instanceSemanal = renderVendedorChart(ctx, vendasSemana); break;
    }
}

/** Atualiza o gráfico da div Mensal */
function atualizarGraficoMensal(type) {
    if (instanceMensal) instanceMensal.destroy();
    const ctx = document.getElementById('chartMensal').getContext('2d');
    
    // Para a visão mensal de evolução, não filtramos apenas o mês atual, 
    // para que possamos mostrar a divisão por meses (Jan, Fev, etc)
    const vendasAtivas = dashboardData.vendas.filter(v => v.active);

    switch (type) {
        case 'vendasMensal': instanceMensal = renderVendasMensalChart(ctx, vendasAtivas); break;
        case 'categoria': instanceMensal = renderCategoriaChart(ctx, dashboardData.estoques); break;
    }
}

/** Calcula e exibe as estatísticas nos cards inferiores */
function processarEstatisticas(estoques, vendas) {
    const moneyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

    const hoje = new Date();
    const inicioMes = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
    const inicioSemana = new Date(hoje);
    inicioSemana.setDate(hoje.getDate() - hoje.getDay());
    inicioSemana.setHours(0,0,0,0);

    const vendasDoMes = vendas.filter(v => v.active && new Date(v.dataVenda) >= inicioMes);
    const vendasDaSemana = vendas.filter(v => v.active && new Date(v.dataVenda) >= inicioSemana);

    // 1. Estoque Crítico
    const criticoEl = document.getElementById('stats-estoque-critico');
    const itensCriticos = estoques.filter(e => e.quantidade < 5);
    if (criticoEl) {
        criticoEl.innerHTML = itensCriticos.length > 0 
            ? itensCriticos.slice(0, 3).map(e => `<p class="mb-1">⚠️ ${e.produto.nomeProduto} (${e.quantidade} un)</p>`).join('')
            : '<p class="text-success">✅ Tudo sob controle</p>';
    }

    // 2. Melhor Vendedor (Base Mensal)
    const vendedores = {};
    vendasDoMes.forEach(v => {
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

    // 3. Ticket Médio (Base Mensal - apenas vendas reais)
    const vendasComerciais = vendasDoMes.filter(v => parseFloat(v.valorTotal) > 0);
    const totalReceita = vendasComerciais.reduce((acc, v) => acc + (parseFloat(v.valorTotal) || 0), 0);
    const ticketMedio = vendasComerciais.length > 0 ? totalReceita / vendasComerciais.length : 0;
    document.getElementById('stats-ticket-medio').textContent = moneyFormatter.format(ticketMedio);

    // 4. Valor Total em Estoque
    const valorEstoque = estoques.reduce((acc, e) => acc + ((e.quantidade || 0) * (parseFloat(e.produto.valorUnitario) || 0)), 0);
    document.getElementById('stats-valor-estoque').textContent = moneyFormatter.format(valorEstoque);

    // 5. Média de Atendimentos (Base: Semana Atual)
    const diaDaSemana = hoje.getDay() + 1; // 1 a 7
    const mediaAtendimentos = (vendasDaSemana.length / diaDaSemana).toFixed(1);
    const elAtendimentos = document.getElementById('stats-media-atendimentos');
    if (elAtendimentos) elAtendimentos.textContent = `${mediaAtendimentos} / dia`;

    renderQuickLists(estoques, vendas);
}

function sortByValueDesc(items, accessor) {
    return [...items].sort((a, b) => (accessor(b) || 0) - (accessor(a) || 0));
}

function renderQuickLists(estoques, vendas) {
    const topProdutos = sortByValueDesc(estoques, item => item.quantidade || 0)
        .slice(0, 5)
        .map(item => ({
            nome: item.produto?.nomeProduto || 'Produto desconhecido',
            quantidade: item.quantidade || 0
        }));
    const produtosList = document.getElementById('top-produtos-list');
    if (produtosList) {
        produtosList.innerHTML = topProdutos.length > 0
            ? topProdutos.map(item => `
                <li>
                    <span>${item.nome}</span>
                    <strong>${item.quantidade} un</strong>
                </li>
            `).join('')
            : '<li>Nenhum produto disponível</li>';
    }

    const ultimasVendas = vendas
        .filter(v => v.active)
        .sort((a, b) => new Date(b.dataVenda) - new Date(a.dataVenda))
        .slice(0, 5)
        .map(venda => ({
            data: venda.dataVenda ? shortDateFormatter.format(new Date(venda.dataVenda)) : 'Data indisponível',
            valor: currencyFormatter.format(parseFloat(venda.valorTotal) || 0)
        }));

    const vendasList = document.getElementById('ultimas-vendas-list');
    if (vendasList) {
        vendasList.innerHTML = ultimasVendas.length > 0
            ? ultimasVendas.map(venda => `
                <li>
                    <span>${venda.data}</span>
                    <strong>${venda.valor}</strong>
                </li>
            `).join('')
            : '<li>Nenhuma venda recente</li>';
    }
}

/** 1. Gráfico de Estoque por Produto (Barra Vertical) */
function renderEstoqueChart(ctx, dados) {
    const itensOrdenados = sortByValueDesc(dados, item => item.quantidade).slice(0, 10);
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: itensOrdenados.map(d => d.produto.nomeProduto),
            datasets: [{
                label: 'Qtd em Estoque',
                data: itensOrdenados.map(d => d.quantidade),
                backgroundColor: itensOrdenados.map(d => d.quantidade < 5 ? '#e74c3c' : colors.primary),
                borderColor: colors.primary,
                borderWidth: 1
            }]
        },
        options: {
            ...chartDefaults,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: context => `${context.dataset.label}: ${context.formattedValue} unidades`
                    }
                }
            },
            scales: {
                y: { beginAtZero: true, grid: { color: colors.grid }, ticks: { color: colors.text } },
                x: { ticks: { color: colors.text }, grid: { display: false } }
            }
        }
    });
}

/** 2. Distribuição por Categoria (Doughnut) - Baseado no Estoque Atual */
function renderCategoriaChart(ctx, estoques) {
    const categorias = {};
    estoques.forEach(item => {
        const cat = item.produto?.tipoProduto || 'Outros';
        categorias[cat] = (categorias[cat] || 0) + (item.quantidade || 0);
    });

    const labels = Object.keys(categorias);
    const values = Object.values(categorias);

    return new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: colors.palette,
                borderWidth: 0
            }]
        },
        options: {
            ...chartDefaults,
            plugins: {
                legend: { position: 'bottom', labels: { color: colors.text } },
                tooltip: {
                    callbacks: {
                        label: context => `${context.label}: ${context.parsed} unidades`
                    }
                }
            }
        }
    });
}

/** 3. Formas de Pagamento (Pie) */
function renderPagamentoChart(ctx, vendas) {
    const pagamentos = {};
    vendas.filter(v => v.active).forEach(v => {
        const forma = v.resumoPagamento || v.formaPagamento || 'Outros';
        const valor = parseFloat(v.valorTotal) || 0;
        pagamentos[forma] = (pagamentos[forma] || 0) + valor;
    });

    const entries = Object.entries(pagamentos).sort((a, b) => b[1] - a[1]);
    const labels = entries.map(entry => entry[0]);
    const values = entries.map(entry => entry[1]);

    return new Chart(ctx, {
        type: 'pie',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: ['#28a745', '#007bff', '#ffc107', '#D4AF37', '#A0522D'],
                borderWidth: 0
            }]
        },
        options: {
            ...chartDefaults,
            plugins: {
                legend: { position: 'bottom', labels: { color: colors.text } },
                tooltip: {
                    callbacks: {
                        label: context => `${context.label}: ${currencyFormatter.format(context.parsed)}`
                    }
                }
            }
        }
    });
}

/** 4. Desempenho por Vendedor (Barra Horizontal) */
function renderVendedorChart(ctx, vendas) {
    const vendedores = {};
    vendas.filter(v => v.active).forEach(v => {
        const nome = v.usuario?.nome || v.user?.nome || 'Sistema';
        const valor = parseFloat(v.valorTotal) || 0;
        vendedores[nome] = (vendedores[nome] || 0) + valor;
    });

    const entries = Object.entries(vendedores).sort((a, b) => b[1] - a[1]);
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: entries.map(entry => entry[0]),
            datasets: [{
                label: 'Total Vendido (R$)',
                data: entries.map(entry => entry[1]),
                backgroundColor: '#DAA520'
            }]
        },
        options: {
            ...chartDefaults,
            indexAxis: 'y',
            plugins: {
                tooltip: {
                    callbacks: {
                        label: context => `${context.dataset.label}: ${currencyFormatter.format(context.parsed.x)}`
                    }
                }
            },
            scales: {
                x: { ticks: { color: colors.text, callback: value => currencyFormatter.format(value) }, grid: { color: colors.grid } },
                y: { ticks: { color: colors.text } }
            }
        }
    });
}

/** 5. Vendas no Tempo - Semana Atual (Line) */
function renderVendasTempoChart(ctx, vendas) {
    const diasSemana = {};
    const hoje = new Date();
    const domingo = new Date(hoje);
    domingo.setDate(hoje.getDate() - hoje.getDay());

    for (let i = 0; i < 7; i++) {
        const d = new Date(domingo);
        d.setDate(domingo.getDate() + i);
        diasSemana[d.toLocaleDateString('pt-BR').slice(0, 5)] = 0;
    }

    vendas.filter(v => v.active).forEach(v => {
        const data = new Date(v.dataVenda).toLocaleDateString('pt-BR').slice(0, 5);
        const valor = parseFloat(v.valorTotal) || 0;
        if (diasSemana[data] !== undefined) diasSemana[data] += valor;
    });

    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: Object.keys(diasSemana),
            datasets: [{
                label: 'Faturamento Semana (R$)',
                data: Object.values(diasSemana),
                borderColor: colors.primary,
                backgroundColor: 'rgba(212, 175, 55, 0.1)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            ...chartDefaults,
            scales: { 
                y: { 
                    beginAtZero: true,
                    ticks: { 
                        color: colors.text,
                        callback: (value) => currencyFormatter.format(value)
                    }, 
                    grid: { color: colors.grid } 
                }, 
                x: { ticks: { color: colors.text } } 
            }
        }
    });
}

/** 6. Evolução Mensal - Divisão por Meses do Ano (Bar) */
function renderVendasMensalChart(ctx, vendas) {
    const faturamentoMensal = {};
    const meses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
    
    // Inicializa os últimos 6 meses com zero
    const hoje = new Date();
    for (let i = 5; i >= 0; i--) {
        const d = new Date(hoje.getFullYear(), hoje.getMonth() - i, 1);
        const label = meses[d.getMonth()] + '/' + d.getFullYear().toString().slice(-2);
        faturamentoMensal[label] = 0;
    }

    vendas.forEach(v => {
        const d = new Date(v.dataVenda);
        const label = meses[d.getMonth()] + '/' + d.getFullYear().toString().slice(-2);
        if (faturamentoMensal[label] !== undefined) {
            faturamentoMensal[label] += (parseFloat(v.valorTotal) || 0);
        }
    });

    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(faturamentoMensal),
            datasets: [{
                label: 'Total por Mês (R$)',
                data: Object.values(faturamentoMensal),
                backgroundColor: colors.palette[1],
                borderRadius: 5
            }]
        },
        options: {
            ...chartDefaults,
            scales: { 
                y: { 
                    beginAtZero: true,
                    ticks: { 
                        color: colors.text,
                        callback: (value) => currencyFormatter.format(value)
                    }, 
                    grid: { color: colors.grid } 
                }, 
                x: { ticks: { color: colors.text } } 
            }
        }
    });
}