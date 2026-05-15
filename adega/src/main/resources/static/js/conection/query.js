import { 
    AuthenticationError, 
    ForbiddenError, 
    NotFoundError, 
    ConflictError, 
    ValidationError,
    InternalServerError,
    AppError 
} from '../exception/exceptions.js'; // Ajustado para o caminho real fornecido no contexto

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
            let corpoErro = null;
            let mensagemErro = `Erro na requisição: ${response.status}`;

            try {
                corpoErro = await response.json();
                if (corpoErro && corpoErro.message) {
                    mensagemErro = corpoErro.message;
                }
            } catch (e) {
                mensagemErro += ` ${response.statusText}`;
            }

            // Mapeamento de status HTTP para classes de exceção personalizadas
            switch (response.status) {
                case 400: throw new ValidationError(mensagemErro, corpoErro);
                case 401:
                    localStorage.removeItem('jwt_token');
                    window.location.href = '/auth/logout';
                    throw new AuthenticationError(mensagemErro, corpoErro);
                case 403: throw new ForbiddenError(mensagemErro, corpoErro);
                case 404: throw new NotFoundError(mensagemErro, corpoErro);
                case 409: throw new ConflictError(mensagemErro, corpoErro);
                case 500: throw new InternalServerError(mensagemErro, corpoErro);
                default:
                    throw new AppError(mensagemErro, response.status, corpoErro);
            }
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