import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'; // Importa componentes do React Router para navegação
import Usuarios from './Usuarios'; // Importa o componente Usuarios
import './App.css';

// Componente da página inicial
function Home() {
  return (
    <div>
      <h1>Bem-vindo à Adega</h1>
      <p>Site da Adega</p>
      <Link to="/usuarios">Ver Usuários</Link> {/* Link para navegar para a página de usuários */}
    </div>
  );
}

// Componente principal da aplicação
function App() {
  return (
    <Router> {/* Envolve a aplicação com o Router para habilitar navegação */}
      <nav> {/* Barra de navegação simples */}
        <Link to="/">Home</Link> | <Link to="/usuarios">Usuários</Link> {/* Links para as rotas */}
      </nav>
      <Routes> {/* Define as rotas da aplicação */}
        <Route path="/" element={<Home />} /> {/* Rota para a página inicial */}
        <Route path="/usuarios" element={<Usuarios />} /> {/* Rota para a página de usuários */}
      </Routes>
    </Router>
  );
}

export default App;
