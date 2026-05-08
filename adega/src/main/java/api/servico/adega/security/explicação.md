# Documentação de Segurança - Projeto Adega UMC

Este documento descreve como a segurança da aplicação foi implementada, integrando o Spring Security, JWT (JSON Web Token) e o seu sistema de exceções personalizadas.

## 1. O Modelo de Dados (`Usuario.java`)
A classe `Usuario` funciona como a ponte entre o seu banco de dados e o motor de segurança do Spring.
- **Interface UserDetails**: Ao implementar esta interface, o Java entende que o seu `Usuario` é uma entidade de segurança válida. Isso permite que o Spring Security acesse a senha e o e-mail diretamente da sua entidade.
- **getAuthorities()**: Define as permissões. Atualmente, todos os usuários recebem o perfil `ROLE_USER`, o que resolveu o erro de compilação no filtro de segurança.
- **isEnabled()**: Este método está conectado ao seu atributo `isActive`. Se você marcar um usuário como inativo no banco, o sistema de segurança negará o acesso dele automaticamente.

## 2. A Camada de Serviço (`UsuarioServiceImpl.java`)
O método `loadUserByUsername` é o coração da busca de credenciais.
- **Integração com Exceções**: Substituímos a exceção padrão do Spring pela sua `ResourceNotFoundException`. 
- **Resultado**: Se alguém tentar logar com um e-mail que não existe, o seu `GlobalExceptionHandler` captura o erro e envia um JSON padronizado com status **404**, mantendo a identidade visual de erros da sua API.

## 3. Configuração Central (`SecurityConfig.java`)
Aqui definimos a "estratégia de defesa" da API:
- **Stateless (Sem Estado)**: A API não guarda sessões ou cookies. Ela é otimizada para ser rápida, validando apenas o Token JWT enviado pelo frontend em cada requisição.
- **Permissões de Rota**: Configuramos para que apenas o `/auth/login` seja público. Qualquer outra funcionalidade da adega exige que o usuário esteja autenticado.
- **BCrypt**: Definimos que o sistema deve usar o algoritmo BCrypt para validar as senhas, garantindo que elas nunca fiquem expostas em texto puro.

## 4. O Filtro de Segurança (`SecurityFilter.java`)
Este componente funciona como um "guarda" posicionado na entrada de cada requisição:
1. Ele verifica se há um token no cabeçalho `Authorization`.
2. Ele utiliza o `TokenService` para validar se o token é legítimo e não expirou.
3. Se o token for válido, ele busca o usuário e o coloca no "Contexto de Segurança", permitindo que a requisição siga para o Controller.

## 5. Carga Inicial de Dados (`UsuarioDataLoader.java`)
Para facilitar o acesso inicial ao sistema, implementamos um carregador de dados:
- **Admin Padrão**: Cria automaticamente o usuário `admin@admin.com` com a senha `admin123` caso ele não exista.

## 6. Por que estas mudanças foram feitas?
- Para resolver o erro de **Type Mismatch** (Usuario não era reconhecido como UserDetails).
- Para resolver o erro de **Undefined Method** (getAuthorities não existia na sua model).
- Para unificar o tratamento de erros usando suas funcionalidades de Exception.
- Para garantir que a API aceite apenas requisições JSON bem formatadas nos endpoints de autenticação.

## 7. Sugestões de Melhorias Futuras

Para tornar o sistema ainda mais robusto e profissional, aqui estão alguns pontos que podem ser implementados:

### 7.1. Injeção de Dependência via Construtor
Substituir o uso de `@Autowired` em campos no `AuthController` e `AuthServiceImpl` por construtores. Isso segue as melhores práticas do Spring, facilita a escrita de testes unitários, garante a imutabilidade com campos `final` e evita problemas de dependência circular.

### 7.2. Controle de Acesso por Perfis (RBAC)
Atualmente, todos os usuários possuem o perfil `ROLE_USER`. Uma melhoria seria criar uma tabela de "Perfis" ou um `Enum` na entidade `Usuario` para distinguir entre `ADMIN` (pode excluir produtos/vendas) e `USER` (apenas consulta e realiza vendas). Com isso, poderíamos usar a anotação `@PreAuthorize("hasRole('ADMIN')")` nos Controllers.

### 7.3. Refinamento do Tratamento de Exceções
- **BadCredentialsException**: Adicionar um tratador no `GlobalExceptionHandler` para retornar um erro **401 Unauthorized** com mensagem amigável quando o e-mail ou senha estiverem incorretos.
- **HttpMediaTypeNotSupportedException**: Tratar explicitamente erros de `Content-Type` inválido para evitar o erro genérico 415 (Unsupported Media Type), garantindo que o cliente saiba que deve enviar `application/json`.

### 7.4. Configuração de CORS
Para permitir que um frontend (como um app em React ou Angular) acesse esta API, será necessário configurar o CORS no `SecurityConfig`. Sem isso, o navegador bloqueará as requisições por segurança.

### 7.5. Renovação de Tokens (Refresh Token)
Implementar uma lógica de Refresh Token para que o usuário não precise fazer login novamente toda vez que o token JWT expirar, aumentando a segurança e a usabilidade.

### 7.6. Auditoria de Acesso
Utilizar o Spring Data JPA Auditing para registrar automaticamente quem criou ou alterou um registro de venda, utilizando o usuário autenticado no contexto de segurança.

### 7.7. Documentação com Swagger (OpenAPI)
Configurar o Swagger para suportar autenticação JWT. Isso permite que desenvolvedores testem endpoints protegidos inserindo o token apenas uma vez no botão "Authorize", facilitando o fluxo de testes.

---
*Documentação criada para suporte ao desenvolvimento da Adega UMC.*