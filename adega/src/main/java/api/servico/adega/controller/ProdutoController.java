package api.servico.adega.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.servico.adega.dto.requests.ProdutoRequestDTO;
import api.servico.adega.dto.responses.ProdutoResponseDTO;
import api.servico.adega.service.ProdutoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
/*
    Construtor
 */
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Lista todos os produtos
     */
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos() {
        return ResponseEntity.ok(produtoService.listarProdutos());
    }

    /**
     * Busca o Produto pelo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    /**
     * Busca os Produtos pelo tipo
     */
    @GetMapping("/tipo")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorTipo(@RequestParam String tipoProduto) {
        return ResponseEntity.ok(produtoService.buscarPorTipoProduto(tipoProduto));
    }

    /**
     * Busca o Produto pelo nome
     */
    @GetMapping("/nome")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nomeProduto) {
        return ResponseEntity.ok(produtoService.buscarPorNomeProduto(nomeProduto));
    }

    /**
     * Busca o Produto pela quantidade
     */
    @GetMapping("/valor")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorValorUnitario(@RequestParam String valorUnitario) {
        return ResponseEntity.ok(produtoService.buscarPorValorUnitario(valorUnitario));
    }

    /**
     * Cria Produto com base no que é mandado no body em POST
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        ProdutoResponseDTO criado = produtoService.criarProduto(produtoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    /**
     * Atualiza o Produto com base no body em PUT
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, produtoRequestDTO));
    }

    /**
     *  Deleta o Produto com base no ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id) {
        produtoService.excluirProduto(id);
        return ResponseEntity.noContent().build();
    }
}
