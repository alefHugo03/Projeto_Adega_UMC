package api.servico.adega.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.servico.adega.dto.requests.ItemVendaRequestDTO;
import api.servico.adega.dto.responses.ItemVendaResponseDTO;
import api.servico.adega.dto.responses.ProdutoResponseDTO;
import api.servico.adega.dto.responses.UsuarioResponseDTO;
import api.servico.adega.dto.responses.VendaResponseDTO;
import api.servico.adega.exception.ConflictException;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.ItemVenda;
import api.servico.adega.repository.ItemVendaRepository;
import api.servico.adega.repository.ProdutoRepository;
import api.servico.adega.repository.VendaRepository;
import api.servico.adega.service.ItemVendaService;

@Service
@Transactional(readOnly = true)
public class ItemVendaServiceImpl implements ItemVendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemVendaRepository itemVendaRepository;

    public ItemVendaServiceImpl(VendaRepository vendaRepository, ProdutoRepository produtoRepository, ItemVendaRepository itemVendaRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.itemVendaRepository = itemVendaRepository;
    }

    @Override
    public List<ItemVendaResponseDTO> buscarPorIdItemVenda(Long id) {
        ItemVenda itemVenda = itemVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemVenda", "id", id));
        return List.of(toResponseDTO(itemVenda));
    }

    @Override
    public List<ItemVendaResponseDTO> buscarPorIdVenda(Long id) {
        List<ItemVenda> itemVendas = itemVendaRepository.findByVenda_IdVenda(id);
        return itemVendas.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ItemVendaResponseDTO> buscarPorIdProduto(Long id) {
        List<ItemVenda> itemVendas = itemVendaRepository.findByProduto_IdProduto(id);
        return itemVendas.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ItemVendaResponseDTO> buscarPorQuantidadeVendida(int quantidadeVendida) {
        List<ItemVenda> itemVendas = itemVendaRepository.findByQuantidadeVendida(quantidadeVendida);
        return itemVendas.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemVendaResponseDTO criarVenda(ItemVendaRequestDTO itemVendaRequestDTO) {
        if (!itemVendaRepository.findByVenda_IdVenda(itemVendaRequestDTO.getIdVenda()).isEmpty()) {
            throw new ConflictException("A venda já possui um item associado. Cada venda pode ter apenas um item.");
        }
        var venda = vendaRepository.findById(itemVendaRequestDTO.getIdVenda())
                .orElseThrow(() -> new ResourceNotFoundException("Venda", "id", itemVendaRequestDTO.getIdVenda()));
        var produto = produtoRepository.findById(itemVendaRequestDTO.getIdProduto())
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", itemVendaRequestDTO.getIdProduto()));
        ItemVenda itemVenda = new ItemVenda();
        itemVenda.setVenda(venda);
        itemVenda.setProduto(produto);
        itemVenda.setQuantidadeVendida(itemVendaRequestDTO.getQuantidadeVendida());
        ItemVenda saved = itemVendaRepository.save(itemVenda);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ItemVendaResponseDTO editarVenda(Long id, ItemVendaResponseDTO itemVendaResponseDTO) {
        ItemVenda itemVenda = itemVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemVenda", "id", id));
        // Assuming we update quantidadeVendida from the DTO
        itemVenda.setQuantidadeVendida(itemVendaResponseDTO.getQuantidadeVendida());
        ItemVenda saved = itemVendaRepository.save(itemVenda);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void excluirVenda(Long id) {
        if (!itemVendaRepository.existsById(id)) {
            throw new ResourceNotFoundException("ItemVenda", "id", id);
        }
        itemVendaRepository.deleteById(id);
    }

    private ItemVendaResponseDTO toResponseDTO(ItemVenda itemVenda) {
        VendaResponseDTO vendaDTO = new VendaResponseDTO(
                itemVenda.getVenda().getIdVenda(),
                itemVenda.getVenda().getFormaPagamento(),
                itemVenda.getVenda().getDataVenda(),
                itemVenda.getVenda().getValorTotal(),
                toUsuarioResponseDTO(itemVenda.getVenda().getUser())
        );
        ProdutoResponseDTO produtoDTO = new ProdutoResponseDTO(
                itemVenda.getProduto().getIdProduto(),
                itemVenda.getProduto().getNomeProduto(),
                itemVenda.getProduto().getTipoProduto(),
                itemVenda.getProduto().getValorUnitario()
        );
        return new ItemVendaResponseDTO(
                itemVenda.getIdItemVenda(),
                vendaDTO,
                produtoDTO,
                itemVenda.getQuantidadeVendida()
        );
    }

    private UsuarioResponseDTO toUsuarioResponseDTO(api.servico.adega.model.Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
