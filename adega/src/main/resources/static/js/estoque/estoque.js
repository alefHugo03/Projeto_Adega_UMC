import logout from '../conection/logout.js'; 
import { carregarEstoque } from './listagem.js';
import { prepararEntrada, prepararEdicao, excluirEstoque } from './acoes.js';

window.logout = logout;
window.prepararEntrada = prepararEntrada;
window.prepararEdicao = prepararEdicao;
window.excluirEstoque = excluirEstoque;

document.addEventListener('DOMContentLoaded', carregarEstoque);