import logout from '../conection/logout.js';
import { carregarHistoricoVendas } from './historico.js';
import { 
    verDetalhesVenda, 
    excluirVenda, 
    prepararEdicaoVenda, 
    salvarVenda, 
    iniciarNovaVenda,
    adicionarLinhaPagamento,
    toggleParcelas,
    atualizarResumoTotal,
    atualizarTotalPago
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
window.fecharModal = fecharModal;

window.addEventListener('click', fecharAoClicarFora);
document.addEventListener('DOMContentLoaded', carregarHistoricoVendas);
