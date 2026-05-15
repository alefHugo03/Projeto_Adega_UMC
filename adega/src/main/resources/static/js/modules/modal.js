export function fecharModal(id = 'modal-detalhes') {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = 'none';
    }
}

export function abrirModal(id = 'modal-detalhes') {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = 'flex';
    }
}

export function fecharAoClicarFora(event) {
    if (event.target.classList.contains('modal')) {
        fecharModal(event.target.id);
    }
}