package api.servico.adega.service;

import java.util.List;

import api.servico.adega.dto.requests.VendaRequestDTO;
import api.servico.adega.dto.responses.VendaResponseDTO;



public interface VendaService {
    

    /* 
    * Lista todas as vendas
    */
    List<VendaResponseDTO> listarVendar();

    /*
    * Buscar por ID
    */
    VendaResponseDTO buscarPorId(Long id);


    /*
    * Criar a venda
    */
    VendaResponseDTO criarVenda(VendaRequestDTO vendaRequestDTO);


    /*
    * Editar uma venda
    */
    VendaResponseDTO editarVenda(Long id, VendaRequestDTO vendaRequestDTO);

    /*
    * Excluir uma venda 
    */
    void excluirVenda(Long id);

    /*
    * Pesquisar a vendo por data 
    */
    List<VendaResponseDTO> buscarPorData(String data);

    /* 
    * Pesquisar por id de usuário 
    */
    List<VendaResponseDTO> buscarPorIdUsuario(Long id);

    /*
    * Pesquisar por forma de pagamento 
    */
    List<VendaResponseDTO> buscarPorFormaPagamento(String formaPagamento);
}
