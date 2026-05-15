package api.servico.adega.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import api.servico.adega.dto.requests.VendaRequestDTO;
import api.servico.adega.dto.responses.VendaResponseDTO;



public interface VendaService {
    

    /* 
    * Lista todas as vendas
    */
    List<VendaResponseDTO> listarVenda();

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
    * Pesquisar a venda por data
    */
    Page<VendaResponseDTO> buscarPorData(String data, Pageable pageable);

    /* 
    * Pesquisar por id de usuário 
    */
    Page<VendaResponseDTO> buscarPorIdUsuario(Long id, Pageable pageable);

    /*
    * Pesquisar por forma de pagamento 
    */
    Page<VendaResponseDTO> buscarPorFormaPagamento(String formaPagamento, Pageable pageable);

    Page<VendaResponseDTO> listarVendasPaginadas(Pageable pageable);
}
