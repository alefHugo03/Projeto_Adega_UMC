import requisitarDados from './query'

async function criar(caminho, corpo = null) {

    const dados = await requisitarDados(caminho, 'POST', corpo);
    
    return dados 
}

export default criar;