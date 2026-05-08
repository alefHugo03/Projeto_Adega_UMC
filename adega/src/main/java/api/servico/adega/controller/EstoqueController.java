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

import api.servico.adega.dto.responses.EstoqueResponseDTO;
import api.servico.adega.service.EstoqueService;

/**
 * Controller REST para a entidade Estoque.
 *
 * Esse controller expõe endpoints HTTP para gerenciar estoques no sistema.
 */
@RestController
@RequestMapping("/api/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;

    /**
     * Injeção do service que encapsula a lógica de negócios.
     */
    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    /**
     * Busca estoque pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<EstoqueResponseDTO>> buscarEstoque(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.buscarEstoque(id));
    }

    /**
     * Busca estoques por ID do produto.
     */
    @GetMapping("/produto/{id}")
    public ResponseEntity<List<EstoqueResponseDTO>> buscarPorProduto(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.buscarPorProduto(id));
    }

    /**
     * Busca estoques por tipo de produto.
     */
    @GetMapping("/tipo")
    public ResponseEntity<List<EstoqueResponseDTO>> buscarPorTipoProduto(@RequestParam String tipoProduto) {
        return ResponseEntity.ok(estoqueService.buscarPorTipoProduto(tipoProduto));
    }

    /**
     * Lista todos os estoques.
     */
    @GetMapping
    public ResponseEntity<List<EstoqueResponseDTO>> listarTodosEstoques() {
        return ResponseEntity.ok(estoqueService.listarTodosEstoques());
    }

    /**
     * Adiciona um novo item ao estoque.
     */
    @PostMapping
    public ResponseEntity<EstoqueResponseDTO> adicionarAoEstoque(@RequestBody EstoqueResponseDTO estoqueResponseDTO) {
        EstoqueResponseDTO criado = estoqueService.adicionarAoEstoque(estoqueResponseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    /**
     * Atualiza um item do estoque existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> atualizarEstoque(
        @PathVariable Long id, @RequestBody EstoqueResponseDTO estoqueResponseDTO) {
        return ResponseEntity.ok(estoqueService.atualizarEstoque(id, estoqueResponseDTO));
    }

    /**
     * Exclui um item do estoque pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirEstoque(@PathVariable Long id) {
        estoqueService.excluirEstoque(id);
        return ResponseEntity.noContent().build();
    }
}
