# Projeto Adega UMC

Sistema de gerenciamento de adega (wine cellar) desenvolvido como projeto acadêmico para a Universidade de Mogi das Cruzes (UMC).

## Tecnologias Utilizadas

- **Backend**: Java 21 com Spring Boot
- **Frontend**: React com Vite
- **Banco de Dados**: MySQL 8
- **Containerização**: Docker e Docker Compose

## Estrutura do Projeto

- `adega/`: API REST em Spring Boot
- `site-adega/`: Interface web em React
- `docker-compose.yml`: Configuração dos containers
- `documentação/`: Arquivos de documentação do projeto

## Pré-requisitos

- Docker e Docker Compose instalados
- Git

## Como Executar

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   cd Projeto_Adega_UMC
   ```

2. Execute os containers:
   ```bash
   docker compose up --build
   ```

3. Acesse as aplicações:
   - Frontend: http://localhost:5173
   - API: http://localhost:8080
   - Banco de dados: localhost:3307 (usuário: adega_user, senha: adega_password)

## Desenvolvimento

### Backend
- Navegue para `adega/`
- Execute `mvn clean compile` para compilar
- Execute `mvn test` para rodar testes

### Frontend
- Navegue para `site-adega/`
- Execute `npm install` para instalar dependências
- Execute `npm run dev` para iniciar o servidor de desenvolvimento
- Execute `npm run build` para build de produção

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto é para fins educacionais.