package api.servico.adega.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.servico.adega.dto.requests.VendaRequestDTO;
import api.servico.adega.dto.responses.VendaResponseDTO;
import api.servico.adega.service.VendaService;
import jakarta.validation.Valid;

/**
 * Controller REST para a entidade Venda.
 *
 * Esse controller expõe endpoints HTTP para criar, listar, buscar,
 * atualizar e excluir vendas no sistema.
 */
@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    /**
     * Injeção do service que encapsula a lógica de negócios.
     */
    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    /**
     * Lista todas as vendas.
     */
    @GetMapping // Endpoint principal para listar todas as vendas com paginação
    public ResponseEntity<Page<VendaResponseDTO>> listarVendasPaginadas(Pageable pageable) {
        return ResponseEntity.ok(vendaService.listarVendasPaginadas(pageable));
    }

    /**
     * Busca uma venda pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    /**
     * Busca vendas por data.
     */
    @GetMapping("/por-data") // Alterado para usar @RequestParam para consistência com outros filtros
    public ResponseEntity<Page<VendaResponseDTO>> buscarPorData(@RequestParam String data, Pageable pageable) {
        return ResponseEntity.ok(vendaService.buscarPorData(data, pageable));
    }

    /**
     * Busca vendas por ID do usuário.
     */
    @GetMapping("/por-usuario") // Alterado para usar @RequestParam
    public ResponseEntity<Page<VendaResponseDTO>> buscarPorIdUsuario(@RequestParam Long idUsuario, Pageable pageable) {
        return ResponseEntity.ok(vendaService.buscarPorIdUsuario(idUsuario, pageable));
    }

    /**
     * Busca vendas por forma de pagamento.
     */
    @GetMapping("/por-forma-pagamento")
    public ResponseEntity<Page<VendaResponseDTO>> buscarPorFormaPagamento(@RequestParam String formaPagamento, Pageable pageable) {
        return ResponseEntity.ok(vendaService.buscarPorFormaPagamento(formaPagamento, pageable));
    }

    /**
     * Recebe um DTO de criação de venda e devolve a venda criada.
     */
    @PostMapping
    public ResponseEntity<VendaResponseDTO> criarVenda(@Valid @RequestBody VendaRequestDTO vendaRequestDTO) {
        VendaResponseDTO criada = vendaService.criarVenda(vendaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    /**
     * Atualiza uma venda existente com base no ID e nos dados recebidos.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendaResponseDTO> atualizarVenda(
        @PathVariable Long id, @Valid @RequestBody VendaRequestDTO vendaRequestDTO) {
        return ResponseEntity.ok(vendaService.editarVenda(id, vendaRequestDTO));
    }

    /**
     * Exclui uma venda existente pelo ID.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirVenda(@PathVariable Long id) {
        vendaService.excluirVenda(id);
        return ResponseEntity.noContent().build();
    }
}