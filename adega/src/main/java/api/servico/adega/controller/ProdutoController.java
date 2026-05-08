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

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos() {
        return ResponseEntity.ok(produtoService.listarProdutos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorTipo(@RequestParam String tipoProduto) {
        return ResponseEntity.ok(produtoService.buscarPorTipoProduto(tipoProduto));
    }

    @GetMapping("/nome")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nomeProduto) {
        return ResponseEntity.ok(produtoService.buscarPorNomeProduto(nomeProduto));
    }

    @GetMapping("/valor")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorValorUnitario(@RequestParam String valorUnitario) {
        return ResponseEntity.ok(produtoService.buscarPorValorUnitario(valorUnitario));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        ProdutoResponseDTO criado = produtoService.criarProduto(produtoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, produtoRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id) {
        produtoService.excluirProduto(id);
        return ResponseEntity.noContent().build();
    }
}
