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
    // Se o alvo do clique for um elemento com a classe 'modal', fechamos ele usando o seu próprio ID
    if (event.target.classList.contains('modal')) {
        fecharModal(event.target.id);
    }
}