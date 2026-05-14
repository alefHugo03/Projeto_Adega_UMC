package api.servico.adega.service.impl;

import api.servico.adega.service.ProdutoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.servico.adega.dto.requests.ProdutoRequestDTO;
import api.servico.adega.dto.responses.ProdutoResponseDTO;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.Estoque;
import api.servico.adega.model.Produto;
import api.servico.adega.repository.EstoqueRepository;
import api.servico.adega.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository, EstoqueRepository estoqueRepository) {
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Override
    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoRepository.findAll()
                .stream()
                .filter(Produto::isActive)
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("produto", "id", id));
        if (!produto.isActive()) {
            throw new ResourceNotFoundException("produto", "id", id);
        }
        return toResponseDTO(produto);
    }

    @Override
    public List<ProdutoResponseDTO> buscarPorTipoProduto(String tipoProduto) {
        List<Produto> produto = produtoRepository.findByTipoProduto(tipoProduto);

        if (produto.isEmpty()) {
            throw new ResourceNotFoundException("produto", "tipoProduto", tipoProduto);
        }

        return produto.stream()
                .filter(Produto::isActive)
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
                .filter(Produto::isActive)
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
                .filter(Produto::isActive)
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO) {
        Produto produto = toEntity(produtoRequestDTO);
        Produto salvo = produtoRepository.save(produto);

        // Inicializa o estoque automaticamente para evitar erros na primeira venda
        Estoque estoqueInicial = new Estoque();
        estoqueInicial.setProduto(salvo);
        estoqueInicial.setQuantidade(0);
        estoqueInicial.setActive(true);
        estoqueRepository.save(estoqueInicial);

        return toResponseDTO(salvo);
    }

    @Override
    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoRequestDTO) {
        Produto produtoExiste = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("produto", "id", id));

        // Impede a edição de um produto desativado
        if (!produtoExiste.isActive()) {
            throw new ResourceNotFoundException("produto", "id", id);
        }

        produtoExiste.setTipoProduto(produtoRequestDTO.getTipoProduto());
        produtoExiste.setNomeProduto(produtoRequestDTO.getNomeProduto());
        // Removido o .toString() pois o campo na entidade agora é BigDecimal
        produtoExiste.setValorUnitario(new BigDecimal(produtoRequestDTO.getValorUnitario()));

        Produto atualizado = produtoRepository.save(produtoExiste);

        return toResponseDTO(atualizado);
    }

    @Override
    @Transactional
    public void excluirProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("produto", "id", id));
        produto.setActive(false);
        produtoRepository.save(produto);
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
        // Removido o .toString() para salvar como BigDecimal
        produto.setValorUnitario(new BigDecimal(dto.getValorUnitario()));
        produto.setActive(true); // Garante que nasce ativo

        return produto;
    }
}
