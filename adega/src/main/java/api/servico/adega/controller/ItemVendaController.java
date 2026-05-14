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

import api.servico.adega.dto.requests.ItemVendaRequestDTO;
import api.servico.adega.dto.responses.ItemVendaResponseDTO;
import api.servico.adega.service.ItemVendaService;
import jakarta.validation.Valid;

/**
 * Controller REST para a entidade ItemVenda.
 *
 * Esse controller expõe endpoints HTTP para gerenciar itens de venda no sistema.
 */
@RestController
@RequestMapping("/api/itemvendas")
public class ItemVendaController {

    private final ItemVendaService itemVendaService;

    /**
     * Injeção do service que encapsula a lógica de negócios.
     */
    public ItemVendaController(ItemVendaService itemVendaService) {
        this.itemVendaService = itemVendaService;
    }

    /**
     * Busca itens de venda pelo ID do item.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemVendaResponseDTO> buscarPorIdItemVenda(@PathVariable Long id) {
        return ResponseEntity.ok(itemVendaService.buscarPorIdItemVenda(id));
    }

    /**
     * Busca itens de venda pelo ID da venda.
     */
    @GetMapping("/venda/{id}")
    public ResponseEntity<List<ItemVendaResponseDTO>> buscarPorIdVenda(@PathVariable Long id) {
        return ResponseEntity.ok(itemVendaService.buscarPorIdVenda(id));
    }

    /**
     * Busca itens de venda pelo ID do produto.
     */
    @GetMapping("/produto/{id}")
    public ResponseEntity<List<ItemVendaResponseDTO>> buscarPorIdProduto(@PathVariable Long id) {
        return ResponseEntity.ok(itemVendaService.buscarPorIdProduto(id));
    }

    /**
     * Busca itens de venda pela quantidade vendida.
     */
    @GetMapping("/quantidade")
    public ResponseEntity<List<ItemVendaResponseDTO>> buscarPorQuantidadeVendida(@RequestParam int quantidadeVendida) {
        return ResponseEntity.ok(itemVendaService.buscarPorQuantidadeVendida(quantidadeVendida));
    }

    /**
     * Cria um novo item de venda.
     */
    @PostMapping
    public ResponseEntity<ItemVendaResponseDTO> criarVenda(@Valid @RequestBody ItemVendaRequestDTO itemVendaRequestDTO) {
        ItemVendaResponseDTO criado = itemVendaService.criarVenda(itemVendaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    /**
     * Edita um item de venda existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemVendaResponseDTO> editarVenda(
        @PathVariable Long id, @RequestBody ItemVendaResponseDTO itemVendaResponseDTO) {
        return ResponseEntity.ok(itemVendaService.editarVenda(id, itemVendaResponseDTO));
    }

    /**
     * Exclui um item de venda pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirVenda(@PathVariable Long id) {
        itemVendaService.excluirVenda(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/venda/{id}")
    public ResponseEntity<Void> excluirVendaPorIdVenda(@PathVariable Long id) {
        itemVendaService.excluirPorIdVenda(id);
        return ResponseEntity.noContent().build();
    }
}
