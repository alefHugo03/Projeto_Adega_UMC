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
            
            // Armazena o token. No seu AuthController, o campo se chama 'token' dentro do LoginResponseDTO.
            localStorage.setItem('jwt_token', data.token);
            
            console.log('Login realizado com sucesso!');
            // Redireciona para a página home após o sucesso
            window.location.href = '/home'; 
        } else {
            const erro = await response.json();
            alert('Erro ao entrar: ' + (erro.message || 'Verifique suas credenciais.'));
        }
    } catch (error) {
        console.error('Erro na requisição de login:', error);
        alert('Erro ao conectar com o servidor.');
    }
}