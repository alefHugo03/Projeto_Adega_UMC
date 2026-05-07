package api.servico.adega.service.impl;

import api.servico.adega.dto.requests.ProdutoRequestDTO;
import api.servico.adega.dto.responses.ProdutoResponseDTO;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.Produto;
import api.servico.adega.repository.ProdutoRepositoy;

import java.util.List;
import java.util.Optional;

public class ProdutoServiceImpl {

    private final ProdutoRepositoy produtoRepositoy;

    public ProdutoServiceImpl(ProdutoRepositoy produtoRepositoy) {
        this.produtoRepositoy = produtoRepositoy;
    }

    @Override
    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoRepositoy.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepositoy.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("produto", "id", id);
        return toResponseDTO(produto);
    }

    @Override
    public List<ProdutoResponseDTO> buscarPorTipoProduto(String tipoProduto) {
        List<Produto> produto = produtoRepositoy.findByTipoProduto(tipoProduto);

        if (produto.isEmpty()) {
            throw new ResourceNotFoundException("produto", "tipoProduto", tipoProduto);
        }

        return produto.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<ProdutoResponseDTO> buscarPorNomeProduto (String nomeProduto) {
        Optional<Produto> produto = produtoRepositoy.findByNomeProduto(nomeProduto);

        if (produto.isEmpty()) {
            throw new ResourceNotFoundException("produto", "nomeProduto", nomeProduto);
        }

        return produto.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<ProdutoResponseDTO> buscarPorValorUnitario(String valorUnitario) {
        List<Produto> produto = produtoRepositoy.findByValorUnitario(valorUnitario);

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
        Produto salvo = produtoRepositoy.save(produto);
        return toResponseDTO(salvo);

    }




    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getIdProduto(),
                produto.getNomeProduto(),
                produto.getTipoProduto(),
                produto.getValorUnitario()
        );
    }

    private Produto toEntity(Produto dto) {
        Produto produto = new Produto();
        produto.setTipoProduto(dto.getTipoProduto());
        produto.setNomeProduto(dto.getNomeProduto());
        produto.setValorUnitario(dto.getValorUnitario());

        return produto;
    }
}
