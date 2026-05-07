package api.servico.adega.service;

import java.util.List;

import api.servico.adega.dto.responses.EstoqueResponseDTO;

public interface EstoqueService {

    List<EstoqueResponseDTO> buscarEstoque(Long id);

    List<EstoqueResponseDTO> buscarPorProduto(Long id);

    EstoqueResponseDTO adicionarAoEstoque(EstoqueResponseDTO estoqueResponseDTO);

    EstoqueResponseDTO  atualizarEstoque(Long id, EstoqueResponseDTO estoqueResponseDTO);

    void excluirEstoque(Long id);
}
