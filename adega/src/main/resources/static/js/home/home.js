import requisitarDados from '../conection/query.js';
import {
    renderEstoqueChart,
    renderCategoriaChart,
    renderPagamentoChart,
    renderVendedorChart,
    renderVendasTempoChart,
    renderVendasMensalChart
} from './graficos.js';

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
        case 'vendasTempo': {
            const aggregatedData = [];
            const domingo = new Date(hoje);
            domingo.setDate(hoje.getDate() - hoje.getDay());
            domingo.setHours(0, 0, 0, 0); // Início do dia

            // 1. Inicializa os pontos usando TIMESTAMPS (Números) em vez de strings ISO
            for (let i = 0; i < 7; i++) { 
                const d = new Date(domingo);
                d.setDate(domingo.getDate() + i);
                // CORREÇÃO: Guardamos o .getTime() puro no 'x'
                aggregatedData.push({ x: d.getTime(), y: 0 });
            }

            // 2. Agrupa os valores das vendas batendo as datas convertidas para o início do dia
            vendasSemana.filter(v => v.active).forEach(v => {
                const dataVenda = new Date(v.dataVenda);
                // Zera as horas da venda para bater com o dia correto do loop anterior
                dataVenda.setHours(0, 0, 0, 0);
                const timestampVenda = dataVenda.getTime();

                // Encontra a entrada correspondente pelo número do timestamp
                const existingEntry = aggregatedData.find(entry => entry.x === timestampVenda);
                if (existingEntry) {
                    existingEntry.y += (parseFloat(v.valorTotal) || 0);
                }
            });

            // 3. Ordena numericamente os timestamps para a linha não cruzar o gráfico de forma errada
            aggregatedData.sort((a, b) => a.x - b.x);

            // Passa os dados 100% numéricos para o gráfico otimizado
            instanceSemanal = renderVendasTempoChart(ctx, aggregatedData); 
            break;
        }
        case 'pagamento': {
            const pagamentos = {};
            vendasSemana.filter(v => v.active).forEach(v => {
                const forma = v.resumoPagamento || v.formaPagamento || 'Outros';
                pagamentos[forma] = (pagamentos[forma] || 0) + (parseFloat(v.valorTotal) || 0);
            });
            const entries = Object.entries(pagamentos).sort((a, b) => b[1] - a[1]);
            instanceSemanal = renderPagamentoChart(ctx, entries.map(e => e[0]), entries.map(e => e[1]));
            break;
        }
        case 'vendedor': {
            const vendedores = {};
            vendasSemana.filter(v => v.active).forEach(v => {
                const nome = v.usuario?.nome || v.user?.nome || 'Sistema';
                vendedores[nome] = (vendedores[nome] || 0) + (parseFloat(v.valorTotal) || 0);
            });
            instanceSemanal = renderVendedorChart(ctx, Object.entries(vendedores).sort((a, b) => b[1] - a[1]));
            break;
        }
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
        case 'vendasMensal': {
            const faturamentoMensal = {};
            const meses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
            const hoje = new Date();
            for (let i = 5; i >= 0; i--) {
                const d = new Date(hoje.getFullYear(), hoje.getMonth() - i, 1);
                const label = meses[d.getMonth()] + '/' + d.getFullYear().toString().slice(-2);
                faturamentoMensal[label] = 0;
            }
            vendasAtivas.forEach(v => {
                const d = new Date(v.dataVenda);
                const label = meses[d.getMonth()] + '/' + d.getFullYear().toString().slice(-2);
                if (faturamentoMensal[label] !== undefined) faturamentoMensal[label] += (parseFloat(v.valorTotal) || 0);
            });
            instanceMensal = renderVendasMensalChart(ctx, Object.keys(faturamentoMensal), Object.values(faturamentoMensal));
            break;
        }
        case 'categoria': {
            const categorias = {};
            dashboardData.estoques.forEach(item => {
                const cat = item.produto?.tipoProduto || 'Outros';
                categorias[cat] = (categorias[cat] || 0) + (item.quantidade || 0);
            });
            instanceMensal = renderCategoriaChart(ctx, Object.keys(categorias), Object.values(categorias));
            break;
        }
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