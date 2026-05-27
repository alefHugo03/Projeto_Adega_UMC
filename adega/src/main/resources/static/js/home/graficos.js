/**
 * Configurações globais de estilo para os gráficos (Tema Dark/Gold)
 */
const colors = {
    primary: '#D4AF37',
    text: '#F5F5F5',
    grid: '#333',
    palette: ['#D4AF37', '#8B4513', '#DAA520', '#B8860B', '#F5DEB3', '#A0522D']
};

const chartDefaults = {
    responsive: true,
    maintainAspectRatio: false,
    animation: false // ⚡ OTIMIZAÇÃO: Desativa animações globais para renderizar dados massivos instantaneamente
};

const currencyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

/**
 * Validação de carregamento da biblioteca
 */
export function inicializarGraficos() {
    if (typeof Chart === 'undefined') {
        console.error('Erro: A biblioteca Chart.js não foi encontrada. Adicione o script do Chart.js no seu HTML.');
        return false;
    }
    console.log("Chart.js detectado com sucesso. Inicializando funções de renderização...");
    return true;
}

/** * 1. Gráfico de Estoque por Produto (Barra Vertical) 
 */
export function renderEstoqueChart(ctx, itensOrdenados) {
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

/** * 2. Distribuição por Categoria (Doughnut) 
 */
export function renderCategoriaChart(ctx, labels, values) {
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

/** * 3. Formas de Pagamento (Pie) 
 */
export function renderPagamentoChart(ctx, labels, values) {
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

/** * 4. Desempenho por Vendedor (Barra Horizontal) 
 */
export function renderVendedorChart(ctx, entries) {
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

/** * 5. Vendas no Tempo - Semana Atual (Line) 
 */
export function renderVendasTempoChart(ctx, data) {
    return new Chart(ctx, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Faturamento Semana (R$)',
                data: data, // ⚡ Deve vir mapeado como [{x: timestamp, y: valor}, ...] do Java
                borderColor: colors.primary,
                backgroundColor: 'rgba(212, 175, 55, 0.1)',
                fill: true,
                tension: 0, // ⚡ OTIMIZAÇÃO: Linhas retas processam 10x mais rápido que curvas Bezier
                pointRadius: 0, // ⚡ OTIMIZAÇÃO: Não renderiza milhares de pontos visuais na tela
                pointHitRadius: 10,
                pointHoverRadius: 6
            }]
        },
        options: {
            ...chartDefaults,
            parsing: false, // ⚡ OTIMIZAÇÃO EXTREMA: Desativa a checagem interna de tipos do Chart.js
            plugins: {
                decimation: { 
                    enabled: true,
                    algorithm: 'lttb', // Algoritmo inteligente que mantém picos e vales do gráfico intactos
                    samples: 100 // ⚡ Reduz milhares de pontos para apenas 100 elementos de renderização
                },
                legend: { display: false },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    callbacks: {
                        label: context => `Faturamento: ${currencyFormatter.format(context.parsed.y)}`
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        color: colors.text,
                        callback: (value) => currencyFormatter.format(value)
                    },
                    grid: { color: colors.grid }
                },
                x: { 
                    type: 'timeseries', 
                    // ADICIONE ESTE BLOCO 'time' ABAIXO:
                    time: {
                        unit: 'day', // Separa o gráfico dia por dia
                        tooltipFormat: 'dd/MM/yyyy', // Formato de data ao passar o mouse
                        displayFormats: {
                            day: 'dd/MM' // Formato que vai aparecer no eixo X da tela
                        }
                    },
                    ticks: { 
                        color: colors.text,
                        maxRotation: 0 // Mantém os textos retos na horizontal
                    },
                    grid: { color: colors.grid }
                }
            }
        }
    });
}

/** * 6. Evolução Mensal (Bar) 
 */
export function renderVendasMensalChart(ctx, labels, values) {
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Total por Mês (R$)',
                data: values,
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
                x: {
                    ticks: { color: colors.text },
                    grid: { color: colors.grid }
                }
            }
        }
    });
}