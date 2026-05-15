import logout from '../conection/logout.js';
import { loadSidebar } from '../modules/sidebar.js';
import { 
    carregarHistoricoVendas,
    verDetalhesVenda, 
    excluirVenda, 
    prepararEdicaoVenda, 
    salvarVenda, 
    iniciarNovaVenda,
    adicionarLinhaPagamento,
    toggleParcelas,
    atualizarResumoTotal,
    atualizarTotalPago,
    aplicarFiltrosVendas,
    limparFiltrosVendas
} from './acoes.js';
import { abrirModal, fecharModal, fecharAoClicarFora } from '../modules/modal.js';

// Exposição para o escopo global (usado por onclick no HTML)
window.logout = logout;
window.verDetalhesVenda = verDetalhesVenda;
window.excluirVenda = excluirVenda;
window.prepararEdicaoVenda = prepararEdicaoVenda;
window.salvarVenda = salvarVenda;
window.iniciarNovaVenda = iniciarNovaVenda;
window.adicionarLinhaPagamento = adicionarLinhaPagamento;
window.toggleParcelas = toggleParcelas;
window.atualizarResumoTotal = atualizarResumoTotal;
window.atualizarTotalPago = atualizarTotalPago;
window.aplicarFiltrosVendas = aplicarFiltrosVendas;
window.limparFiltrosVendas = limparFiltrosVendas;
window.fecharModal = fecharModal;

window.addEventListener('click', fecharAoClicarFora);
document.addEventListener('DOMContentLoaded', loadSidebar);
document.addEventListener('DOMContentLoaded', carregarHistoricoVendas);
