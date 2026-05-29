/**
 * Configurações globais de estilo para os gráficos (Tema Premium Dark/Gold)
 */
const colors = {
    primary: '#D4AF37', // Dourado
    primaryAlpha: 'rgba(212, 175, 55, 0.8)',
    primaryGlow: 'rgba(212, 175, 55, 0.1)',
    text: '#A0A0A0', 
    grid: 'rgba(255, 255, 255, 0.03)', 
    tooltipBg: 'rgba(0, 0, 0, 0.8)',
    // Paleta em tons de ouro e madeira para gráficos de pizza/doughnut
    palette: ['#D4AF37', '#B8860B', '#DAA520', '#FCC200', '#F5DEB3', '#8B4513']
};

const currencyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

/**
 * Configurações Base para todos os gráficos
 */
const chartDefaults = {
    responsive: true,
    maintainAspectRatio: false,
    animation: {
        duration: 800,
        easing: 'easeOutQuart'
    },
    plugins: {
        legend: { display: false },
        tooltip: {
            backgroundColor: colors.tooltipBg,
            titleColor: '#D4AF37',
            bodyColor: '#FFF',
            borderColor: 'rgba(212, 175, 55, 0.3)',
            borderWidth: 1,
            padding: 12,
            cornerRadius: 8,
            displayColors: false,
            backdropFilter: 'blur(4px)'
        }
    },
    interaction: {
        mode: 'index',
        intersect: false,
    }
};

/**
 * Função para destruir um gráfico se ele já existir (Evita travamentos e vazamento de RAM)
 */
function limparGraficoExistente(ctx) {
    if (!ctx) return;
    const chartExistente = Chart.getChart(ctx);
    if (chartExistente) {
        chartExistente.destroy();
    }
}

/**
 * Cria um efeito "Fade" do dourado sólido para o transparente nas barras
 */
function criarGradiente(ctx) {
    if (!ctx) return colors.primary;
    const canvasCtx = ctx.canvas.getContext('2d');
    const gradient = canvasCtx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, colors.primaryAlpha);
    gradient.addColorStop(1, colors.primaryGlow);
    return gradient;
}

export function inicializarGraficos() {
    if (typeof Chart === 'undefined') {
        console.error('Erro: Chart.js não encontrado.');
        return false;
    }
    return true;
}

/** * 1. Gráfico de Estoque por Produto (Barra Vertical) 
 */
export function renderEstoqueChart(ctx, itensOrdenados) {
    if (!ctx) return null;
    limparGraficoExistente(ctx);

    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: itensOrdenados.map(item => item.produto?.nomeProduto || 'N/A').slice(0, 10),
            datasets: [{
                label: 'Em Estoque',
                data: itensOrdenados.map(item => item.quantidade).slice(0, 10),
                backgroundColor: criarGradiente(ctx),
                borderColor: colors.primary,
                borderWidth: { top: 2, right: 0, bottom: 0, left: 0 },
                borderRadius: { topLeft: 6, topRight: 6, bottomLeft: 0, bottomRight: 0 },
                barPercentage: 0.6
            }]
        },
        options: {
            ...chartDefaults,
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: colors.grid, drawBorder: false },
                    ticks: { color: colors.text, stepSize: 10 }
                },
                x: {
                    grid: { display: false },
                    ticks: { color: colors.text, maxRotation: 45, minRotation: 45 }
                }
            }
        }
    });
}

/** * 2. Evolução Mensal (Barra) 
 */
export function renderVendasMensalChart(ctx, labels, values) {
    if (!ctx) return null;
    limparGraficoExistente(ctx);

    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Total Faturado',
                data: values,
                backgroundColor: criarGradiente(ctx),
                borderColor: colors.primary,
                borderWidth: { top: 2, right: 0, bottom: 0, left: 0 },
                borderRadius: { topLeft: 6, topRight: 6 },
                barPercentage: 0.7
            }]
        },
        options: {
            ...chartDefaults,
            plugins: {
                ...chartDefaults.plugins,
                tooltip: {
                    ...chartDefaults.plugins.tooltip,
                    callbacks: { label: (context) => currencyFormatter.format(context.raw) }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: colors.grid, drawBorder: false },
                    ticks: {
                        color: colors.text,
                        callback: (value) => currencyFormatter.format(value)
                    }
                },
                x: {
                    grid: { display: false },
                    ticks: { color: colors.text }
                }
            }
        }
    });
}

/** * 3. Vendas por Categoria (Doughnut) 
 */
export function renderCategoriaChart(ctx, labels, values) {
    if (!ctx) return null;
    limparGraficoExistente(ctx);

    return new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: colors.palette,
                borderWidth: 2,
                borderColor: '#151515', 
                hoverOffset: 4
            }]
        },
        options: {
            ...chartDefaults,
            plugins: {
                legend: { display: true, position: 'right', labels: { color: colors.text } }
            },
            cutout: '70%'
        }
    });
}

/** * 4. Vendas por Pagamento (Doughnut) 
 */
export function renderPagamentoChart(ctx, labels, values) {
    if (!ctx) return null;
    limparGraficoExistente(ctx);

    return new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: colors.palette.slice().reverse(),
                borderWidth: 2,
                borderColor: '#151515',
                hoverOffset: 4
            }]
        },
        options: {
            ...chartDefaults,
            plugins: {
                legend: { display: true, position: 'bottom', labels: { color: colors.text } }
            },
            cutout: '70%'
        }
    });
}

/** * 5. Vendas por Vendedor (Barra Horizontal) 
 */
export function renderVendedorChart(ctx, labels, values) {
    if (!ctx) return null;
    limparGraficoExistente(ctx);

    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Vendas',
                data: values,
                backgroundColor: criarGradiente(ctx),
                borderRadius: 4
            }]
        },
        options: {
            ...chartDefaults,
            indexAxis: 'y', // Transforma a barra em horizontal
            plugins: {
                ...chartDefaults.plugins,
                tooltip: {
                    ...chartDefaults.plugins.tooltip,
                    callbacks: { label: (context) => currencyFormatter.format(context.raw) }
                }
            },
            scales: {
                x: { 
                    grid: { color: colors.grid }, 
                    ticks: { color: colors.text, callback: (value) => currencyFormatter.format(value) } 
                },
                y: { grid: { display: false }, ticks: { color: colors.text } }
            }
        }
    });
}

/** * 6. Vendas no Tempo - Semana Atual (Line) 
 */
export function renderVendasTempoChart(ctx, labels, values) {
    if (!ctx) return null;
    limparGraficoExistente(ctx);

    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels, 
            datasets: [{
                label: 'Faturamento Semana (R$)',
                data: values,
                borderColor: colors.primary,
                backgroundColor: 'rgba(212, 175, 55, 0.1)',
                fill: true,
                tension: 0.4, // Curva suave
                pointBackgroundColor: colors.primary,
                pointBorderColor: '#000',
                pointRadius: 4, 
                pointHoverRadius: 6
            }]
        },
        options: {
            ...chartDefaults,
            plugins: {
                ...chartDefaults.plugins,
                legend: { display: false },
                tooltip: {
                    ...chartDefaults.plugins.tooltip,
                    callbacks: {
                        label: context => `Faturamento: ${currencyFormatter.format(context.raw)}`
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
                    ticks: { color: colors.text },
                    grid: { display: false }
                }
            }
        }
    });
}