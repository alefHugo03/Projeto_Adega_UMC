/**
 * Realiza o logout do usuário limpando o token do localStorage e dos Cookies.
 */
function logout() {
    // 1. Remove o token do localStorage (usado para chamadas de API)
    localStorage.removeItem('jwt_token');

    // 2. Redireciona para a rota de logout do servidor.
    // O Spring Security irá enviar o cabeçalho para limpar o cookie 'jwt_token' e redirecionar para '/login'.
    window.location.href = '/auth/logout';
}

export default logout;