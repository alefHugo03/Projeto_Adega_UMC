package api.servico.adega.service.impl;

import api.servico.adega.dto.requests.ItemVendaRequestDTO;
import api.servico.adega.dto.responses.ItemVendaResponseDTO;
import api.servico.adega.dto.responses.ProdutoResponseDTO;
import api.servico.adega.dto.responses.VendaResponseDTO;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.ItemVenda;
import api.servico.adega.repository.ItemVendaRepository;
import api.servico.adega.repository.VendaRepository;
import api.servico.adega.service.ItemVendaService;
import api.servico.adega.model.Venda;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemVendaServiceImpl implements ItemVendaService {

    private final ItemVendaRepository itemVendaRepository;
    private final VendaRepository vendaRepository;

    public ItemVendaServiceImpl(ItemVendaRepository itemVendaRepository, VendaRepository vendaRepository) {
        this.itemVendaRepository = itemVendaRepository;
        this.vendaRepository = vendaRepository;
    }

    @Override
    public List<ItemVendaResponseDTO> buscarPorIdVenda(Long vendaId) {
        // Retorna apenas os itens vinculados à venda que estão ativos
        // Nota: Certifique-se que o método findByVenda_IdVenda existe no seu Repository
        return itemVendaRepository.findAll()
                .stream()
                .filter(item -> item.isActive() && item.getVenda() != null && item.getVenda().getIdVenda().equals(vendaId))
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public ItemVendaResponseDTO buscarPorIdItemVenda(Long id) {
        ItemVenda item = itemVendaRepository.findById(id)
                .filter(ItemVenda::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("ItemVenda", "id", id));
        return toResponseDTO(item);
    }

    @Override
    public List<ItemVendaResponseDTO> buscarPorIdProduto(Long produtoId) {
        return itemVendaRepository.findAll().stream()
                .filter(item -> item.isActive() && item.getProduto() != null && item.getProduto().getIdProduto().equals(produtoId))
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<ItemVendaResponseDTO> buscarPorQuantidadeVendida(int quantidade) {
        return itemVendaRepository.findAll().stream()
                .filter(item -> item.isActive() && item.getQuantidadeVendida() == quantidade)
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ItemVendaResponseDTO criarVenda(ItemVendaRequestDTO dto) {
        // Implementação básica de criação (ajuste conforme sua lógica de negócio)
        ItemVenda novo = new ItemVenda();
        novo.setQuantidadeVendida(dto.getQuantidadeVendida());
        novo.setActive(true);
        return toResponseDTO(itemVendaRepository.save(novo));
    }

    @Override
    @Transactional
    public ItemVendaResponseDTO editarVenda(Long id, ItemVendaResponseDTO dto) {
        ItemVenda item = itemVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemVenda", "id", id));
        item.setQuantidadeVendida(dto.getQuantidadeVendida());
        return toResponseDTO(itemVendaRepository.save(item));
    }

    @Override
    @Transactional
    public void excluirVenda(Long id) {
        ItemVenda item = itemVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemVenda", "id", id));
        item.setActive(false);

        // Ao desativar o item, também desativa a venda associada
        if (item.getVenda() != null) {
            Venda venda = item.getVenda();
            venda.setActive(false);
            vendaRepository.save(venda);
        }

        itemVendaRepository.save(item);
    }

    @Override
    @Transactional
    public void excluirPorIdVenda(Long vendaId) {
        List<ItemVenda> itens = itemVendaRepository.findAll()
                .stream()
                .filter(item -> item.isActive() && item.getVenda() != null && item.getVenda().getIdVenda().equals(vendaId))
                .toList();
        
        itens.forEach(item -> item.setActive(false));
        itemVendaRepository.saveAll(itens);

        // Desativa a venda principal
        vendaRepository.findById(vendaId).ifPresent(venda -> {
            venda.setActive(false);
            vendaRepository.save(venda);
        });
    }



    private ItemVendaResponseDTO toResponseDTO(ItemVenda item) {
        // O erro ocorria porque você passava boolean e String onde o DTO esperava outros DTOs.
        VendaResponseDTO vendaDTO = null;
        if (item.getVenda() != null) {
            vendaDTO = new VendaResponseDTO();
            vendaDTO.setIdVenda(item.getVenda().getIdVenda());
            String resumoPagamento = item.getVenda().getPagamentos().stream()
                    .map(p -> p.getFormaPagamento().getDescricao())
                    .distinct()
                    .collect(Collectors.joining(", "));
            vendaDTO.setFormaPagamento(resumoPagamento);
        }

        ProdutoResponseDTO produtoDTO = null;
        if (item.getProduto() != null) {
            produtoDTO = new ProdutoResponseDTO(
                item.getProduto().getIdProduto(),
                item.getProduto().getNomeProduto(),
                item.getProduto().getTipoProduto(),
                item.getProduto().getValorUnitario()
            );
        }

        return new ItemVendaResponseDTO(
                item.getIdItemVenda(),
                vendaDTO,
                produtoDTO,
                item.getQuantidadeVendida(),
                item.isActive()
        );
    }
}