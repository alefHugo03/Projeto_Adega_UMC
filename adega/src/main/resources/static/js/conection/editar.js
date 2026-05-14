import requisitarDados from './query'

async function editar(caminho, corpo = null) {
    try {
        const dados = await requisitarDados(caminho, 'PUT', corpo);
        return dados
    }
    catch (error) {
        console.error('Erro ao requisitar dados:', error);
        throw error;
    }
}

export default editar;