import requisitarDados from '../conection/query.js';
import { handleAppError } from '../exception/exceptions.js';

/**
 * Inicializa os gráficos de faturamento na home
 */
export async function inicializarDashboards() {
    try {
        // Tente buscar sem os parâmetros de ordenação primeiro para testar se o erro persiste
        // Se o seu campo no Java for diferente de 'dataHoraVenda', ajuste o nome abaixo
        const response = await requisitarDados('/api/vendas?size=500', 'GET');
        
        // Verifica se a resposta é um objeto de página (Spring Data) ou uma lista simples
        const vendas = Array.isArray(response) ? response : (response.content || []);

        if (vendas.length === 0) return;

        renderizarGraficoMensal(vendas);
        renderizarGraficoSemanal(vendas);
    } catch (error) {
        handleAppError(error);
    }
}

function renderizarGraficoMensal(vendas) {
    const faturamentoPorMes = {};

    vendas.forEach(venda => {
        const data = new Date(venda.dataVenda);
        if (isNaN(data)) return; // Ignora se a data for inválida
        const mesAno = data.toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' });
        faturamentoPorMes[mesAno] = (faturamentoPorMes[mesAno] || 0) + (parseFloat(venda.valorTotal) || 0);
    });

    const labels = Object.keys(faturamentoPorMes).reverse();
    const dados = Object.values(faturamentoPorMes).reverse();

    criarChart('chartFaturamentoMensal', 'Faturamento Mensal (R$)', labels, dados, 'rgba(54, 162, 235, 0.6)');
}

function renderizarGraficoSemanal(vendas) {
    const faturamentoPorSemana = {};

    vendas.forEach(venda => {
        const data = new Date(venda.dataVenda);
        if (isNaN(data)) return; // Ignora se a data for inválida
        const primeiraDataDoAno = new Date(data.getFullYear(), 0, 1);
        const semana = Math.ceil((((data - primeiraDataDoAno) / 86400000) + primeiraDataDoAno.getDay() + 1) / 7);
        const labelSemana = `Semana ${semana} - ${data.getFullYear()}`;
        
        faturamentoPorSemana[labelSemana] = (faturamentoPorSemana[labelSemana] || 0) + (parseFloat(venda.valorTotal) || 0);
    });

    // Pega apenas as últimas 8 semanas para não poluir o gráfico
    const labels = Object.keys(faturamentoPorSemana).slice(-8);
    const dados = Object.values(faturamentoPorSemana).slice(-8);

    criarChart('chartFaturamentoSemanal', 'Faturamento por Semana (R$)', labels, dados, 'rgba(75, 192, 192, 0.6)');
}

function criarChart(elementId, label, labels, dados, color) {
    const canvas = document.getElementById(elementId);
    if (!canvas) return;

    // Verifica se já existe um gráfico instanciado neste canvas e o destrói
    const existingChart = Chart.getChart(canvas);
    if (existingChart) {
        existingChart.destroy();
    }

    new Chart(canvas, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: label,
                data: dados,
                backgroundColor: color,
                borderColor: color.replace('0.6', '1'),
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: (value) => 'R$ ' + value.toLocaleString('pt-BR')
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: (context) => `Total: R$ ${context.parsed.y.toLocaleString('pt-BR')}`
                    }
                }
            }
        }
    });
}