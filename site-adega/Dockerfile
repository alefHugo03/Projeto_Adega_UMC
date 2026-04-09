FROM node:20-alpine

WORKDIR /app

# Copia as dependências e instala antes do restante do código para aproveitar cache.
COPY package*.json ./
RUN npm install

COPY . .

# Porta padrão do Vite.
EXPOSE 5173

# Inicia o servidor de desenvolvimento do frontend na porta 5173 e expõe em 0.0.0.0 para o host.
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "5173"]
