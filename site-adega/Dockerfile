FROM node:20-bookworm-slim

WORKDIR /app

# Copia as dependências e instala antes do restante do código para aproveitar cache.
COPY package*.json ./
RUN npm install --legacy-peer-deps && npm cache clean --force

COPY . .

# Define a porta interna padrão, mas permite que seja sobrescrita.
ENV PORT=5173
EXPOSE ${PORT}

# Usa a variável PORT para iniciar o Vite.
CMD ["sh", "-c", "npm run dev -- --host 0.0.0.0 --port ${PORT}"]
