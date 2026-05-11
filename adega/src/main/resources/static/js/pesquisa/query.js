

async function requisitarDados(caminho, metodo) {
    // 1. Recupera o token do localStorage
    const token = localStorage.getItem('jwt_token');

    if (!token) {
        console.error('Nenhum token encontrado. Redirecionando para login...');
        window.location.href = '/login';
        return;
    }

    try {
        const response = await fetch(caminho, {
            method: metodo,
            headers: {
                // 2. Adiciona o token no formato que o seu SecurityFilter espera
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            // Se a resposta não for OK (ex: 401, 403, 404, 500), joga um erro.
            // O status 403 (Forbidden) pode ser tratado aqui ou no catch.
            // Para 401 (Unauthorized), o redirecionamento para login já é feito se o token não existe.
            // Se o token existe mas é inválido/expirado, o 401/403 virá do backend.
            throw new Error(`Erro na requisição: ${response.status} ${response.statusText}`);
        }

        const valor = await response.json();
        return valor; // Retorna os dados para serem usados por outras funções
        
    } catch (error) {
        console.error('Erro ao requisitar dados:', error);
        throw error; // Re-lança o erro para que a função chamadora possa tratá-lo
    }
}

export default requisitarDados;