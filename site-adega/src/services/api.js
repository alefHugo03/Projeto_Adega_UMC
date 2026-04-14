import axios from 'axios';

// Configuração centralizada do Axios
const api = axios.create({
  // Define a URL base buscando do .env ou usando o padrão do Spring Boot
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080'
});

export default api;