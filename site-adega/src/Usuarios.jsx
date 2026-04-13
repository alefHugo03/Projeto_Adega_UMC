import { useEffect, useState } from 'react';
import axios from 'axios';

// Componente para exibir a lista de usuários
// Faz uma requisição GET para /api/usuarios e exibe os dados em uma lista
function Usuarios() {
  const [usuarios, setUsuarios] = useState([]); // Estado para armazenar a lista de usuários
  const [loading, setLoading] = useState(true); // Estado para controlar o carregamento
  const [error, setError] = useState(null); // Estado para armazenar erros

  // Define a URL base da API (Usa variável de ambiente ou fallback para localhost)
  const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  // useEffect para buscar os usuários quando o componente é montado
  useEffect(() => {
    axios.get(`${API_URL}/api/usuarios`) // Requisição para o endpoint completo
      .then(response => {
        setUsuarios(response.data); // Atualiza o estado com os dados recebidos
        setLoading(false); // Desativa o loading
      })
      .catch(error => {
        console.error('Erro na requisição API:', error);
        setError('Erro ao carregar usuários'); // Define mensagem de erro
        setLoading(false); // Desativa o loading
      });
  }, []); // Array vazio significa que roda apenas uma vez, no mount

  // Renderização condicional baseada no estado
  if (loading) return <p>Carregando...</p>; // Mostra loading enquanto carrega
  if (error) return <p>{error}</p>; // Mostra erro se houver

  // Renderiza a lista de usuários
  return (
    <div>
      <h1>Lista de Usuários</h1>
      <ul>
        {usuarios.map(usuario => (
          <li key={usuario.id}> {/* Chave única para cada item da lista */}
            {usuario.nome} - {usuario.email} {/* Exibe nome e email do usuário */}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Usuarios;