 document.getElementById('formLogin').addEventListener('submit', (e) => {
            e.preventDefault();
            const email = document.getElementById('inputEmail').value;
            const senha = document.getElementById('inputSenha').value;
            realizarLogin(email, senha);
        });