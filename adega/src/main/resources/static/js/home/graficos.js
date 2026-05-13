export function inicializarGraficos() {
    if (typeof Chart === 'undefined') {
        console.error('Erro: A biblioteca Chart.js não foi encontrada. Adicione o script do Chart.js no seu HTML.');
        return;
    }

    const ctxEstoque = document.getElementById('estoqueChart')?.getContext('2d');
    if (ctxEstoque) {
        new Chart(ctxEstoque, {
            type: 'bar',
            data: {
                labels: ['IPA', 'Lager', 'Stout', 'Whisky Single Malt', 'Whisky Bourbon'],
                datasets: [{
                    label: 'Quantidade em Estoque',
                    data: [45, 30, 25, 15, 10], // Dados de exemplo focados no novo nicho
                    backgroundColor: '#D4AF37', // Dourado nos gráficos
                    borderRadius: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { labels: { color: '#F5F5F5' } } // Texto da legenda em branco suave
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { color: '#F5F5F5' }, // Números do eixo Y
                        grid: { color: '#333' }    // Linhas de grade sutis
                    },
                    x: {
                        ticks: { color: '#F5F5F5' }, // Rótulos do eixo X
                        grid: { color: '#333' }
                    }
                }
            }
        });
    }

    const ctxCategoria = document.getElementById('categoriaChart')?.getContext('2d');
    if (ctxCategoria) {
        new Chart(ctxCategoria, {
            type: 'doughnut',
            data: {
                labels: ['Cervejas', 'Whiskys', 'Outros'],
                datasets: [{
                    data: [60, 30, 10], // Proporção ajustada
                    backgroundColor: ['#D4AF37', '#8B6508', '#333333'], // Dourado, Dourado Escuro e Grafite
                    borderWidth: 2,
                    borderColor: '#1A1A1A'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom', labels: { color: '#F5F5F5' } }
                }
            }
        });
    }
}