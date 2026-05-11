/**
 * Realiza o logout do usuário, removendo o token JWT do localStorage
 * e redirecionando para a página de login.
 */
function logout() {
    console.log('Realizando logout...');
    localStorage.removeItem('jwt_token');
    localStorage.clear(); // Limpa qualquer outra sujeira
    window.location.href = '/login';
}

export default logout;