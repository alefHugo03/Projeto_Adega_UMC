import requisitarDados from './query.js';

async function excluir(caminho, tipo, callbackSucesso) {
    if (confirm(`Tem certeza que deseja excluir este registro de ${tipo}?`)) {
        try {
            await requisitarDados(caminho, 'DELETE');
            alert(`${tipo} excluído(a) com sucesso!`);
            
            if (callbackSucesso && typeof callbackSucesso === 'function') {
                await callbackSucesso();
            }
        } catch (error) {
            console.error(`Erro ao excluir ${tipo}:`, error);
            if (error.message.includes('409') || error.message.includes('Conflict')) {
                alert(`Não é possível excluir: existem produtos vinculados a esta ${tipo}.`);
            } else {
                alert(`Não foi possível excluir o/a ${tipo}.`);
            }
        }
    }
}

export default excluir;