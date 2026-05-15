import { 
    handleAppError, 
    AuthenticationError, 
    InvalidCredentialsError,
    InternalServerError, 
    ValidationError, 
    AppError 
} from '../exception/exceptions.js';

/**
 * Configura o evento de submit do formulário quando a página carrega.
 */
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formLogin');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('inputEmail').value;
            const senha = document.getElementById('inputSenha').value;
            await realizarLogin(email, senha);
        });
    }
});

/**
 * Realiza o login enviando email e senha para a API.
 * Salva o token JWT no localStorage em caso de sucesso.
 */
async function realizarLogin(email, senha) {
    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, senha })
        });

        if (response.ok) {
            const data = await response.json();
            
            // Armazena no localStorage (para o query.js) e no Cookie (para o Spring Security bloquear páginas)
            localStorage.setItem('jwt_token', data.token);
            document.cookie = `jwt_token=${data.token}; path=/; SameSite=Strict`;
            
            // Redireciona para a página home após o sucesso
            window.location.href = '/home'; 
        } else {
            let corpoErro = null;
            let mensagem = 'Erro inesperado no login.';
            
            try {
                corpoErro = await response.json();
                mensagem = corpoErro?.message || mensagem;
            } catch (e) {
                throw new InternalServerError(`Erro crítico do servidor (Status ${response.status})`);
            }

            // Mapeia o erro para a classe correta para o handleAppError funcionar
            switch (response.status) {
                case 400: throw new ValidationError(mensagem, corpoErro);
                case 401: throw new InvalidCredentialsError(mensagem, corpoErro);
                case 403: throw new ForbiddenError(mensagem, corpoErro);
                case 500: throw new InternalServerError(mensagem, corpoErro);
                default: throw new AppError(mensagem, response.status, corpoErro);
            }
        }
    } catch (error) {
        // Utiliza a sua função centralizada para mostrar o alerta visual
        handleAppError(error);
    }
}