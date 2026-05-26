/**
 * Realiza o logout do usuário limpando o token do localStorage e dos Cookies.
 */
function logout() {
    localStorage.removeItem('jwt_token');

    window.location.href = '/auth/logout';
}

export default logout;