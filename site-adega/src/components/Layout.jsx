import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';

function Layout() {
  return (
    <div className="page-container">
      <Header />
      <div className="content">
        {/* O Outlet é onde o conteúdo das páginas (Home, Usuarios) será injetado */}
        <Outlet />
      </div>
      <Footer />
    </div>
  );
}

export default Layout;