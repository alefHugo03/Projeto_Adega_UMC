function Footer() {
  return (
    <footer style={{ marginTop: '40px', padding: '1rem', borderTop: '1px solid #ddd', textAlign: 'center', fontSize: '0.9rem', color: '#666' }}>
      <p>&copy; {new Date().getFullYear()} Adega UMC - Sistema de Gerenciamento</p>
    </footer>
  );
}

export default Footer;