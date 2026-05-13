import logout from '../conection/logout.js'; 
import buscarDadosProtegidos from '../conection/query.js';
import { inicializarGraficos } from './graficos.js';

window.logout = logout;
window.buscarDadosProtegidos = buscarDadosProtegidos;

document.addEventListener('DOMContentLoaded', () => {
    console.log("Página Home carregada. Inicializando dashboard...");
    
    if (typeof inicializarGraficos === 'function') {
        inicializarGraficos();
    }
});