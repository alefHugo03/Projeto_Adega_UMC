package api.servico.adega.service;

import api.servico.adega.dto.requests.ItemVendaRequestDTO;
import api.servico.adega.dto.responses.ItemVendaResponseDTO;

import java.util.List;

public interface ItemVendaService {

    List<ItemVendaResponseDTO> buscarPorIdItemVenda(Long id);

    List<ItemVendaResponseDTO> buscarPorIdVenda(Long id);

    List<ItemVendaResponseDTO> buscarPorIdProduto(Long id);

    List<ItemVendaResponseDTO> buscarPorQuantidadeVendida(int quantidadeVendida);

    ItemVendaResponseDTO criarVenda(ItemVendaRequestDTO itemVendaRequestDTO);

    ItemVendaResponseDTO editarVenda(Long id, ItemVendaResponseDTO itemVendaResponseDTO);

    void excluirVenda(Long id);
}
