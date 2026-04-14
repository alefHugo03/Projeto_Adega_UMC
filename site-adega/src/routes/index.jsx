import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Usuarios from '../pages/Usuarios';
import Home from '../pages/Home';
import Layout from '../components/Layout';

// Centraliza todas as rotas da aplicação
function AppRoutes() {
  return (
    <Router>
      <Routes>
        {/* O Route sem path envolvendo os outros aplica o Layout em todas as páginas filhas */}
        <Route element={<Layout />}>
          <Route path="/" element={<Home />} />
          <Route path="/usuarios" element={<Usuarios />} />
        </Route>
        
        {/* Rota padrão: redireciona para usuários se a rota não existir */}
        <Route path="*" element={<Navigate to="/usuarios" />} />
      </Routes>
    </Router>
  );
}

export default AppRoutes;