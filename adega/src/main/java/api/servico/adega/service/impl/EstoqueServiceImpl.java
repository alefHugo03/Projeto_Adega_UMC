package api.servico.adega.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.servico.adega.dto.responses.EstoqueResponseDTO;
import api.servico.adega.dto.responses.ProdutoResponseDTO;
import api.servico.adega.exception.BadRequestException;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.Estoque;
import api.servico.adega.model.Produto;
import api.servico.adega.repository.EstoqueRepository;
import api.servico.adega.repository.ProdutoRepository;
import api.servico.adega.service.EstoqueService;

@Service
@Transactional(readOnly = true)
public class EstoqueServiceImpl implements EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    public EstoqueServiceImpl(EstoqueRepository estoqueRepository, ProdutoRepository produtoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public List<EstoqueResponseDTO> buscarEstoque(Long id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque", "id", id));
        return List.of(toResponseDTO(estoque));
    }

    @Override
    public List<EstoqueResponseDTO> buscarPorProduto(Long id) {
        List<Estoque> estoques = estoqueRepository.findByProduto_IdProduto(id);
        return estoques.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<EstoqueResponseDTO> buscarPorTipoProduto(String tipoProduto) {
        List<Estoque> estoques = estoqueRepository.findByProduto_TipoProduto(tipoProduto);
        return estoques.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<EstoqueResponseDTO> listarTodosEstoques() {
        List<Estoque> estoques = estoqueRepository.findAll();
        return estoques.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EstoqueResponseDTO adicionarAoEstoque(EstoqueResponseDTO estoqueResponseDTO) {
        if (estoqueResponseDTO.getQuantidade() <= 0) {
            throw new BadRequestException("A quantidade deve ser maior que zero.");
        }
        Produto produto = produtoRepository.findById(estoqueResponseDTO.getProduto().getIdProduto())
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", estoqueResponseDTO.getProduto().getIdProduto()));

        // Busca se já existe um registro de estoque para este produto
        // Usamos findByProduto_IdProduto e pegamos o primeiro se existir
        Estoque estoque = estoqueRepository.findByProduto_IdProduto(produto.getIdProduto())
                .stream()
                .findFirst()
                .orElse(null);
        
        if (estoque != null) {
            // Se já existe, somamos a nova quantidade à atual (Entrada)
            estoque.setQuantidade(estoque.getQuantidade() + estoqueResponseDTO.getQuantidade());
        } else {
            // Se não existe, criamos o registro pela primeira vez
            estoque = new Estoque();
            estoque.setProduto(produto);
            estoque.setQuantidade(estoqueResponseDTO.getQuantidade());
        }

        Estoque saved = estoqueRepository.save(estoque);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public EstoqueResponseDTO atualizarEstoque(Long id, EstoqueResponseDTO estoqueResponseDTO) {
        if (estoqueResponseDTO.getQuantidade() <= 0) {
            throw new BadRequestException("A quantidade deve ser maior que zero.");
        }
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque", "id", id));
        Produto produto = produtoRepository.findById(estoqueResponseDTO.getProduto().getIdProduto())
                .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", estoqueResponseDTO.getProduto().getIdProduto()));
        estoque.setProduto(produto);
        estoque.setQuantidade(estoqueResponseDTO.getQuantidade());
        Estoque saved = estoqueRepository.save(estoque);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void excluirEstoque(Long id) {
        if (!estoqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estoque", "id", id);
        }
        estoqueRepository.deleteById(id);
    }

    private EstoqueResponseDTO toResponseDTO(Estoque estoque) {
        ProdutoResponseDTO produtoDTO = new ProdutoResponseDTO(
                estoque.getProduto().getIdProduto(),
                estoque.getProduto().getNomeProduto(),
                estoque.getProduto().getTipoProduto(),
                estoque.getProduto().getValorUnitario()
        );
        return new EstoqueResponseDTO(estoque.getIdEstoque(), produtoDTO, estoque.getQuantidade());
    }
}
