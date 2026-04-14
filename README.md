# Projeto Adega UMC

O **Projeto Adega UMC** é uma aplicação Full Stack robusta desenvolvida para o gerenciamento de uma adega. O sistema permite o controle de usuários e integra uma API REST moderna com uma interface web responsiva, servindo como projeto acadêmico para a Universidade de Mogi das Cruzes.

## 🚀 Funcionalidades

### Backend (API)
- **CRUD de Usuários**: Gestão completa (Criação, Leitura, Atualização e Exclusão).
- **Busca Avançada**: Filtros por nome (case insensitive) e busca única por e-mail.
- **Tratamento de Erros**: Handlers globais para respostas de erro padronizadas (RFC 7807).
- **Documentação**: Interface Swagger/OpenAPI integrada para testes de endpoints.
- **CORS**: Configurado para integração segura com o frontend React.

### Frontend (Web)
- **Navegação SPA**: Utilização de React Router para uma experiência sem recarregamento.
- **Interface Intuitiva**: Dashboard simples para visualização e navegação entre módulos.
- **Consumo de API**: Integração assíncrona com o serviço Spring Boot.

## Tecnologias Utilizadas

### ☕ Backend
- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.x
- **Persistência**: Spring Data JPA / Hibernate
- **Documentação**: SpringDoc OpenAPI (Swagger)
- **Utilitários**: Lombok, Maven

### ⚛️ Frontend
- **Framework**: React 18
- **Build Tool**: Vite
- **Roteamento**: React Router DOM
- **Estilização**: CSS3 (Modular)

### 🗄️ Infraestrutura
- **Banco de Dados**: MySQL 8.0
- **Containerização**: Docker & Docker Compose

## Estrutura do Projeto

```text
Projeto_Adega_UMC/
├── adega/              # Código fonte do Backend (Maven/Spring)
├── site-adega/         # Código fonte do Frontend (Node/React)
├── docker-compose.yml  # Orquestração de containers (App + DB)
└── documentação/       # Artefatos e manuais do projeto
```

## Pré-requisitos

Para rodar o projeto, você precisará de:
- Docker e Docker Compose instalados.
- Git para clonagem.

## ⚙️ Como Executar

### Via Docker (Recomendado)

1. Clone o repositório e acesse a pasta:
   ```bash
   git clone https://github.com/seu-usuario/Projeto_Adega_UMC.git
   cd Projeto_Adega_UMC
   ```

2. Suba o ambiente completo:
   ```bash
   docker compose up --build
   ```

3. URLs de acesso:
   - **Frontend**: http://localhost:5173
   - **API Backend**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **Database**: `localhost:3307` (User: `adega_user`, Pass: `adega_password`)

## 🛠️ Desenvolvimento Local

### Backend
Se desejar rodar fora do Docker:
1. Certifique-se de ter um MySQL rodando localmente.
2. Ajuste o `application.properties` com suas credenciais.
3. Execute via Maven:
   ```bash
   cd adega
   mvn spring-boot:run
   ```

### Frontend
1. Instale as dependências:
   ```bash
   cd site-adega
   npm install
   ```
2. Inicie em modo de desenvolvimento:
   ```bash
   npm run dev
   ```

## 📖 Documentação da API

A API segue o padrão RESTful. Os principais endpoints incluem:
- `GET /api/usuarios`: Lista todos ou filtra por nome.
- `POST /api/usuarios`: Cria um novo usuário.
- `GET /api/usuarios/email/{email}`: Busca por e-mail específico.

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto foi desenvolvido exclusivamente para fins educacionais (UMC).