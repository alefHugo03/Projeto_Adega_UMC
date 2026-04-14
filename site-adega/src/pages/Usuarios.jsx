import { useEffect, useState } from 'react';
import api from '../services/api';

// Componente para exibir a lista de usuários
// Faz uma requisição GET para /api/usuarios e exibe os dados em uma lista
function Usuarios() {
  const [usuarios, setUsuarios] = useState([]); // Estado para armazenar a lista de usuários
  const [loading, setLoading] = useState(true); // Estado para controlar o carregamento
  const [error, setError] = useState(null); // Estado para armazenar erros

  // useEffect para buscar os usuários quando o componente é montado
  useEffect(() => {
    api.get('/usuarios') // Removido o '/api' redundante
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

  // Renderiza a estrutura da página com pré-carregamento (Skeleton/Loading)
  return (
    <main style={{ minHeight: '60vh', padding: '0 1rem' }}>
      <h1>Lista de Usuários</h1>
      
      {loading ? (
        <p>⏳ Carregando dados do servidor...</p>
      ) : error ? (
        <p style={{ color: 'red' }}>{error}</p>
      ) : (
        <ul>
          {usuarios.map(usuario => (
            <li key={usuario.id}>
              <strong>{usuario.nome}</strong> - {usuario.email}
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}

export default Usuarios;