

async function requisitarDados(caminho, metodo, corpo = null) {
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
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: corpo ? JSON.stringify(corpo) : null
        });

        if (!response.ok) {
            // Se o servidor retornar 401 (Não autorizado) ou 403 (Proibido), 
            // significa que o token expirou ou é inválido.
            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('jwt_token');
                window.location.href = '/auth/logout';
            }

            // Tenta capturar a mensagem de erro detalhada do GlobalExceptionHandler
            let mensagemErro = `Erro na requisição: ${response.status}`;
            try {
                const corpoErro = await response.json();
                if (corpoErro && corpoErro.message) {
                    mensagemErro = corpoErro.message;
                }
            } catch (e) {
                mensagemErro += ` ${response.statusText}`;
            }

            throw new Error(mensagemErro);
        }

        // Verifica se há conteúdo na resposta antes de tentar converter para JSON
        // Isso evita erros em respostas 204 (No Content) comuns em DELETE ou PUT
        const texto = await response.text();
        const valor = texto ? JSON.parse(texto) : null;
        
        return valor; // Retorna os dados para serem usados por outras funções
        
    } catch (error) {
        console.error('Erro ao requisitar dados:', error);
        throw error; // Re-lança o erro para que a função chamadora possa tratá-lo
    }
}

export default requisitarDados;