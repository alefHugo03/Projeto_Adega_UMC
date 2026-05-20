# 🍷 Adego Systems - Gestão de Adega

Este é um sistema completo de gerenciamento para adegas, focado no nicho de bebidas premium (Cervejas Artesanais e Whiskys). O projeto foi desenvolvido como parte de um projeto acadêmico para a faculdade UMC.

A aplicação permite o controle de estoque, registro de vendas, visualização de dashboards analíticos e possui um sistema de autenticação robusto.

---

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.4.2**
- **Spring Data JPA** (Persistência de dados)
- **Spring Security + JWT (auth0)** (Segurança e Autenticação)
- **Spring Validation** (Validação de dados)
- **Springdoc OpenAPI (Swagger)** (Documentação da API)
- **MySQL 8.0** (Banco de dados relacional)
- **Lombok** (Produtividade no código)

### Frontend
- **Thymeleaf** (Motor de templates para renderização no servidor)
- **Vanilla JavaScript** (Lógica do lado do cliente e integração com API)
- **Chart.js** (Visualização de dados e gráficos)
- **CSS3 Flexbox/Grid** (Layout moderno com tema "Dark Mode" e detalhes em dourado)

### Infraestrutura
- **Docker & Docker Compose** (Containerização)
- **Maven** (Gerenciamento de dependências)

---

## 📋 Pré-requisitos

Antes de começar, você precisará ter instalado em sua máquina:
* [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
* [Maven](https://maven.apache.org/download.cgi) (ou use o `mvnw` incluso)
* Docker Desktop
* Um editor de código (recomendado: VS Code com extensões Java ou IntelliJ IDEA)

---

## ⚙️ Configuração do Ambiente

1. **Clone o repositório:**
   ```bash
   git clone <url-do-seu-repositorio>
   cd Projeto_Adega_UMC
   ```

2. **Configuração de Variáveis de Ambiente:**
   Crie um arquivo `.env` na raiz do projeto (ao lado do `docker-compose.yml`) com as seguintes chaves:
   ```env
   MYSQL_ROOT_PASSWORD=suasenha_root
   MYSQL_DATABASE=adega_db
   MYSQL_USER=adega_user
   MYSQL_PASSWORD=suasenha_user
   DB_PORT_HOST=3307
   API_PORT_HOST=8080
   ```

---

## 🐳 Execução via Docker (Recomendado)

O projeto está totalmente configurado para subir o banco de dados e a aplicação com um único comando:

```bash
# Constrói a imagem da API e sobe os containers
docker-compose up --build -d
```

O Docker irá:
1. Subir um container MySQL 8.0 na porta `3307`.
2. Executar um healthcheck no banco de dados.
3. Construir a aplicação Java e aguardar o banco ficar saudável.
4. Disponibilizar a aplicação na porta `8080`.

---

## 💻 Execução Local (Desenvolvimento)

Caso queira rodar a aplicação fora do Docker para desenvolvimento rápido:

1. **Inicie o banco de dados:**
   Você pode subir apenas o banco via Docker:
   ```bash
   docker-compose up db -d
   ```

2. **Execute a aplicação:**
   ```bash
   cd adega
   ./mvnw spring-boot:run
   ```

---

## 📖 Documentação da API

Com a aplicação rodando, você pode acessar a documentação interativa das rotas (Swagger) em:
👉 `http://localhost:8080/swagger-ui/index.html`

---

## 🏗️ Estrutura de Pastas Relevantes

- `/adega/src/main/resources/templates`: Páginas HTML (Thymeleaf).
- `/adega/src/main/resources/static/js`: Lógica de integração e manipulação do DOM.
- `/adega/src/main/resources/static/css`: Estilização (Dashboard, Vendas, Tabelas).
- `/adega/src/main/java`: Código fonte Java (Controllers, Services, Security).

---

## 👥 Autores

* **Alef** - *Desenvolvimento Inicial* - alefHugo03
* **Arthur** - *Desenvolvimento Inicial* - alefHugo03
* **Richard** - *Desenvolvimento Inicial* - alefHugo03
* **Vitor** - *Desenvolvimento Inicial* - alefHugo03
