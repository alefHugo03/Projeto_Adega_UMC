import requisitarDados from '../pesquisa/query.js';
import logout from '../common/logout.js'; 

// Expõe as funções para o escopo global (necessário para o 'onclick' no HTML)
window.logout = logout;

/**
 * Busca dados protegidos de um endpoint da API e os exibe no console.
 * @param {string} caminho - O caminho do endpoint da API.
 */
async function buscarDadosProtegidos(caminho) {
    try {
        const dados = await requisitarDados(caminho, 'GET');
        if (dados) {
            console.log(`Dados de ${caminho}:`, dados);
            alert(`Dados de ${caminho} carregados! Verifique o console.`);
        } else {
            console.warn(`Nenhum dado retornado de ${caminho}.`);
        }
    } catch (error) {
        console.error(`Erro ao buscar dados de ${caminho}:`, error);
        // Se o erro for 401 ou 403, pode ser necessário redirecionar para o login
        if (error.message.includes('401') || error.message.includes('403')) {
            alert('Sessão expirada ou acesso negado. Redirecionando para o login.');
            logout(); // Redireciona para o login
        } else {
            alert(`Erro ao carregar dados de ${caminho}. Verifique o console.`);
        }
    }
}

// Exporta a função para que possa ser chamada diretamente do HTML via onclick
// Novamente, para onclick, é necessário expor globalmente.
window.buscarDadosProtegidos = buscarDadosProtegidos;

console.log('home.js carregado.');