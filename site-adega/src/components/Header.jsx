import { Link } from 'react-router-dom';

function Header() {
  return (
    <header style={{ padding: '1rem', borderBottom: '1px solid #ddd', marginBottom: '20px' }}>
      <nav>
        <Link to="/" style={{ marginRight: '15px', textDecoration: 'none', color: '#007bff' }}>Home</Link>
        <Link to="/usuarios" style={{ textDecoration: 'none', color: '#007bff' }}>Usuários</Link>
      </nav>
    </header>
  );
}

export default Header;