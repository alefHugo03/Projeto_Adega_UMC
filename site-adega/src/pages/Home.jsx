import { Link } from 'react-router-dom';

function Home() {
  return (
    <div>
      <h1>Bem-vindo à Adega</h1>
      <p>Site da Adega</p>
      <Link to="/usuarios">Ver Usuários</Link>
    </div>
  );
}

export default Home;