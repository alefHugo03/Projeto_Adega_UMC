import requisitarDados from '../conection/query.js';
import logout from '../conection/logout.js';
import { carregarHistoricoVendas } from './historico.js';
import { verDetalhesVenda } from './detalhes.js';
import { abrirModal, fecharModal, fecharAoClicarFora } from '../modules/modal.js';

// Exposição para o escopo global (usado por onclick no HTML)
window.logout = logout;
window.verDetalhesVenda = verDetalhesVenda;
window.excluirVenda = excluirVenda;
window.prepararEdicaoVenda = prepararEdicaoVenda;
window.salvarVenda = salvarVenda;
window.iniciarNovaVenda = iniciarNovaVenda; // Garantindo exposição global
window.fecharModal = fecharModal;
window.addEventListener('click', fecharAoClicarFora);

/**
 * Exclui uma venda permanentemente
 */
async function excluirVenda(id) {
    if (confirm('Tem certeza que deseja excluir este registro de venda?')) {
        try {
            await requisitarDados(`/api/vendas/${id}`, 'DELETE');
            alert('Venda excluída com sucesso!');
            await carregarHistoricoVendas(); 
        } catch (error) {
            console.error('Erro ao excluir venda:', error);
            // Tratamento unificado do erro 409 (Conflito de integridade no banco)
            if (error.message.includes('409') || error.message.includes('Conflict')) {
                alert('Não é possível excluir esta venda: existem produtos vinculados a ela. Apague os itens da venda primeiro.');
            } else {
                alert('Não foi possível excluir a venda.');
            }
        }
    }
}

/**
 * Prepara o formulário para edição carregando os dados da venda
 */
async function prepararEdicaoVenda(id) {
    try {
        const venda = await requisitarDados(`/api/vendas/${id}`, 'GET');
        console.log("Dados para edição carregados:", venda);

        const campoId = document.getElementById('venda-id');
        const campoPagamento = document.getElementById('venda-pagamento');
        const tituloModal = document.getElementById('modal-venda-titulo');

        if (campoId) campoId.value = venda.idVenda || venda.id || id;
        if (campoPagamento) campoPagamento.value = venda.formaPagamento;
        if (tituloModal) tituloModal.textContent = "Editar Venda";
        
        const modalElement = document.getElementById('modal-venda');
        if (modalElement) {
            console.log("Abrindo modal de edição...");
            abrirModal('modal-venda');
        } else {
            console.error('Erro crítico: O elemento HTML com ID "modal-venda" não existe.');
            alert('Erro de interface: O formulário de edição (modal-venda) não foi encontrado na página.');
        }
    } catch (error) {
        console.error('Erro ao buscar dados da venda:', error);
        alert('Erro ao carregar dados para edição.');
    }
}

/**
 * Envia os dados do formulário para o servidor (Criar ou Editar)
 */
async function salvarVenda(event) {
    if (event && event.preventDefault) {
        event.preventDefault();
    }
    
    const id = document.getElementById('venda-id')?.value;
    const formaPagamento = document.getElementById('venda-pagamento')?.value;

    if (!formaPagamento) {
        alert('Por favor, selecione uma forma de pagamento.');
        return;
    }

    const dados = {
        formaPagamento: formaPagamento,
        // Aqui estamos simplificando para demonstrar a conexão.
        itens: [] 
    };

    const isEdicao = id && id.trim() !== "";
    const metodo = isEdicao ? 'PUT' : 'POST';
    const url = isEdicao ? `/api/vendas/${id}` : '/api/vendas';

    try {
        await requisitarDados(url, metodo, dados);
        alert(isEdicao ? 'Venda atualizada!' : 'Venda registrada com sucesso!');
        fecharModal('modal-venda');
        await carregarHistoricoVendas();
    } catch (error) {
        console.error('Erro ao salvar venda:', error);
        alert('Erro ao processar a venda. Verifique os campos.');
    }
}

function iniciarNovaVenda() {
    const form = document.getElementById('form-venda');
    if (form) {
        form.reset();
        const campoId = document.getElementById('venda-id');
        if (campoId) campoId.value = ''; 
    }
    
    const tituloModal = document.getElementById('modal-venda-titulo');
    if (tituloModal) {
        tituloModal.textContent = "Nova Venda";
    }

    if (document.getElementById('modal-venda')) {
        abrirModal('modal-venda');
    } else {
        console.error('Modal "modal-venda" não encontrado para nova venda.');
        alert('Erro ao abrir formulário de venda.');
    }
}

document.addEventListener('DOMContentLoaded', carregarHistoricoVendas);
