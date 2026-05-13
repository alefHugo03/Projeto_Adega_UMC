import requisitarDados from '../conection/query.js';
import { carregarEstoque } from './listagem.js';

export function prepararEntrada() {
    alert('Funcionalidade de nova entrada de estoque a ser implementada.');
}

export function prepararEdicao(id) {
    alert(`Ajustar estoque para o item ID: ${id}`);
}

export async function excluirEstoque(id) {
    if (confirm('Deseja realmente remover este registro de estoque?')) {
        try {
            await requisitarDados(`/api/estoques/${id}`, 'DELETE');
            alert('Registro removido com sucesso!');
            carregarEstoque();
        } catch (error) {
            alert('Erro ao excluir o registro de estoque.');
        }
    }
}