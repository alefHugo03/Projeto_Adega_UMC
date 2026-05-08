package api.servico.adega.service.impl;

import api.servico.adega.service.ProdutoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.servico.adega.dto.requests.ProdutoRequestDTO;
import api.servico.adega.dto.responses.ProdutoResponseDTO;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.Produto;
import api.servico.adega.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Override
    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("produto", "id", id));
        return toResponseDTO(produto);
    }

    @Override
    public List<ProdutoResponseDTO> buscarPorTipoProduto(String tipoProduto) {
        List<Produto> produto = produtoRepository.findByTipoProduto(tipoProduto);

        if (produto.isEmpty()) {
            throw new ResourceNotFoundException("produto", "tipoProduto", tipoProduto);
        }

        return produto.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<ProdutoResponseDTO> buscarPorNomeProduto (String nomeProduto) {
        Optional<Produto> produto = produtoRepository.findByNomeProduto(nomeProduto);

        if (produto.isEmpty()) {
            throw new ResourceNotFoundException("produto", "nomeProduto", nomeProduto);
        }

        return List.of(produto.get()).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<ProdutoResponseDTO> buscarPorValorUnitario(String valorUnitario) {
        List<Produto> produto = produtoRepository.findByValorUnitario(new BigDecimal(valorUnitario));

        if (produto.isEmpty()) {
            throw new ResourceNotFoundException("produto", "valorUnitario", valorUnitario);
        }

        return produto.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO) {
        Produto produto = toEntity(produtoRequestDTO);
        Produto salvo = produtoRepository.save(produto);
        return toResponseDTO(salvo);

    }

    @Override
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoRequestDTO) {
        Produto produtoExiste = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("produto", "id", id));
        produtoExiste.setTipoProduto(produtoRequestDTO.getTipoProduto());
        produtoExiste.setNomeProduto(produtoRequestDTO.getNomeProduto());
        produtoExiste.setValorUnitario(new BigDecimal(produtoRequestDTO.getValorUnitario()).toString());

        Produto atualizado = produtoRepository.save(produtoExiste);

        return toResponseDTO(atualizado);
    }

    @Override
    public void excluirProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("produto", "id", id));
        produtoRepository.delete(produto);
    }



    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getIdProduto(),
                produto.getNomeProduto(),
                produto.getTipoProduto(),
                produto.getValorUnitario()
        );
    }

    private Produto toEntity(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setTipoProduto(dto.getTipoProduto());
        produto.setNomeProduto(dto.getNomeProduto());
        produto.setValorUnitario(new BigDecimal(dto.getValorUnitario()).toString());

        return produto;
    }
}
