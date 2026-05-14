import logout from '../conection/logout.js';
import { carregarEstoque, prepararReposicao, salvarEstoque } from './acoes.js';
import { fecharModal, fecharAoClicarFora } from '../modules/modal.js';

// Global exposure
window.logout = logout;
window.prepararReposicao = prepararReposicao;
window.salvarEstoque = salvarEstoque;
window.fecharModal = fecharModal;

window.addEventListener('click', fecharAoClicarFora);

document.addEventListener('DOMContentLoaded', () => {
    carregarEstoque();
});