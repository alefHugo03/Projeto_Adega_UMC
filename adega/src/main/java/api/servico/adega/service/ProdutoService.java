package api.servico.adega.service;

import java.util.List;

import api.servico.adega.dto.requests.ProdutoRequestDTO;
import api.servico.adega.dto.responses.ProdutoResponseDTO;

public interface ProdutoService {

    List<ProdutoResponseDTO> listarProdutos();

    List<ProdutoResponseDTO> buscarPorId(Long id);

    List<ProdutoResponseDTO> buscarPorTipoProduto(String tipoProduto);

    List<ProdutoResponseDTO> buscarPorNomeProduto(String nomeProduto);

    List<ProdutoResponseDTO> buscarPorValorUnitario(String valorUnitario);

    ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO);

    ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoRequestDTO);

    void excluirProduto(Long id);

}
