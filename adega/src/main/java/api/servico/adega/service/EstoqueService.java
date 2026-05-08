package api.servico.adega.service;

import java.util.List;

import api.servico.adega.dto.responses.EstoqueResponseDTO;

public interface EstoqueService {

    List<EstoqueResponseDTO> buscarEstoque(Long id);

    List<EstoqueResponseDTO> buscarPorProduto(Long id);

    List<EstoqueResponseDTO> buscarPorTipoProduto(String tipoProduto);

    List<EstoqueResponseDTO> listarTodosEstoques();

    EstoqueResponseDTO adicionarAoEstoque(EstoqueResponseDTO estoqueResponseDTO);

    EstoqueResponseDTO  atualizarEstoque(Long id, EstoqueResponseDTO estoqueResponseDTO);

    void excluirEstoque(Long id);
}
