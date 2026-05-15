package api.servico.adega.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.servico.adega.dto.requests.PagamentoRequestDTO;
import api.servico.adega.dto.requests.VendaRequestDTO;
import api.servico.adega.dto.responses.UsuarioResponseDTO;
import api.servico.adega.dto.responses.VendaResponseDTO;
import api.servico.adega.enums.FormaPagamento; // Assumindo a existência do repo
import api.servico.adega.exception.BadRequestException;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.Estoque;
import api.servico.adega.model.ItemVenda;
import api.servico.adega.model.PagamentoVenda;
import api.servico.adega.model.Produto;
import api.servico.adega.model.Usuario;
import api.servico.adega.model.Venda;
import api.servico.adega.repository.EstoqueRepository;
import api.servico.adega.repository.ItemVendaRepository;
import api.servico.adega.repository.PagamentoVendaRepository;
import api.servico.adega.repository.ProdutoRepository;
import api.servico.adega.repository.UsuarioRepository;
import api.servico.adega.repository.VendaRepository;
import api.servico.adega.service.VendaService;

@Service
@Transactional(readOnly = true)
public class VendaServiceImpl implements VendaService {

    private final VendaRepository vendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final EstoqueRepository estoqueRepository;
    private final PagamentoVendaRepository pagamentoVendaRepository;

    public VendaServiceImpl(VendaRepository vendaRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository, ItemVendaRepository itemVendaRepository, EstoqueRepository estoqueRepository, PagamentoVendaRepository pagamentoVendaRepository) {
        this.vendaRepository = vendaRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.estoqueRepository = estoqueRepository;
        this.pagamentoVendaRepository = pagamentoVendaRepository;
    }


    @Override
    public List<VendaResponseDTO> listarVenda() {
        return this.vendaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public Page<VendaResponseDTO> listarVendasPaginadas(Pageable pageable) {
        return vendaRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Override
    public VendaResponseDTO buscarPorId(Long id){
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("venda", "id", id));
        return toResponseDTO(venda);
    }

    @Override
    public Page<VendaResponseDTO> buscarPorData(String data, Pageable pageable) {
        LocalDate dataConvertida = LocalDate.parse(data);
        LocalDateTime inicio = dataConvertida.atStartOfDay();
        LocalDateTime fim = dataConvertida.atTime(23, 59, 59, 999_999_999);
        Page<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim, pageable);

        if (vendas.isEmpty()) {
            throw new ResourceNotFoundException("Venda", "data", data);
        }

        return vendas.map(this::toResponseDTO);
    }

    @Override
    public Page<VendaResponseDTO> buscarPorIdUsuario(Long id, Pageable pageable){
        Page<Venda> vendas = vendaRepository.findByUser_Id(id, pageable);
        return vendas.map(this::toResponseDTO);
    }

    
    @Override
    public Page<VendaResponseDTO> buscarPorFormaPagamento(String formaPagamento, Pageable pageable){
        FormaPagamento tipo;
        try {
            tipo = FormaPagamento.paraEnum(formaPagamento);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Forma de pagamento inválida: " + formaPagamento);
        }

        Page<Venda> vendas = vendaRepository.findDistinctByPagamentos_FormaPagamento(tipo, pageable);

        if (vendas.isEmpty()) {
            throw new ResourceNotFoundException("Venda", "formaPagamento", formaPagamento);
        }

        return vendas.map(this::toResponseDTO);
    }

    @Override
    @Transactional
    public VendaResponseDTO criarVenda(VendaRequestDTO vendaRequestDTO){
        Usuario usuario = usuarioRepository.findById(vendaRequestDTO.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", vendaRequestDTO.getIdUser()));

        Venda venda = new Venda();

        // Define a data: usa a informada ou a atual do servidor
        if (vendaRequestDTO.getDataVenda() != null && !vendaRequestDTO.getDataVenda().isBlank()) {
            // OffsetDateTime lida com o formato ISO do JS (com 'Z' no final)
            venda.setDataVenda(OffsetDateTime.parse(vendaRequestDTO.getDataVenda()).toLocalDateTime());
        } else {
            venda.setDataVenda(LocalDateTime.now());
        }

        venda.setUser(usuario);
        venda.setActive(true);
        venda.setValorTotal(BigDecimal.ZERO);
        
        // SALVAR A VENDA PRIMEIRO: Necessário para gerar o ID que será usado nos itens e pagamentos
        venda = vendaRepository.save(venda);

        BigDecimal valorTotalCalculado = BigDecimal.ZERO;

        if (vendaRequestDTO.getItens() != null) {
            for (VendaRequestDTO.ItemVendaRequestDTO itemDTO : vendaRequestDTO.getItens()) {
                Produto produto = produtoRepository.findById(itemDTO.getIdProduto())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", itemDTO.getIdProduto()));

                // --- BAIXA DE ESTOQUE ---
                List<Estoque> estoques = estoqueRepository.findByProduto_IdProduto(produto.getIdProduto());
                if (estoques.isEmpty()) {
                    throw new ResourceNotFoundException("Estoque", "produto", produto.getNomeProduto());
                }
                Estoque estoque = estoques.get(0);
                if (estoque.getQuantidade() < itemDTO.getQuantidade()) {
                    throw new BadRequestException("Estoque insuficiente para o produto: " + produto.getNomeProduto());
                }
                estoque.setQuantidade(estoque.getQuantidade() - itemDTO.getQuantidade());
                estoqueRepository.save(estoque);

                // --- REGISTRO DO ITEM DA VENDA ---
                ItemVenda itemVenda = new ItemVenda();
                itemVenda.setVenda(venda);
                itemVenda.setProduto(produto);
                itemVenda.setQuantidadeVendida(itemDTO.getQuantidade());
                itemVenda.setActive(true);
                itemVendaRepository.save(itemVenda);

                BigDecimal preco = produto.getValorUnitario();
                BigDecimal quantidade = new BigDecimal(itemDTO.getQuantidade());
                valorTotalCalculado = valorTotalCalculado.add(preco.multiply(quantidade));
            }
        }

        // --- PROCESSAMENTO DE PAGAMENTOS ---
        processarPagamentosVenda(venda, vendaRequestDTO.getPagamentos(), valorTotalCalculado);

        venda.setValorTotal(valorTotalCalculado);
        return toResponseDTO(vendaRepository.save(venda)); // Atualiza o valor total no banco
    }

    @Override
    @Transactional
    public VendaResponseDTO editarVenda(Long id, VendaRequestDTO vendaRequestDTO){
        Venda vendaExistente = this.vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda", "id", id));
        // Impede a edição de uma venda desativada
        if (!vendaExistente.isActive()) {
            throw new ResourceNotFoundException("Venda", "id", id);
        }

        // 1. REVERTER ESTOQUE DOS ITENS ANTIGOS E REMOVER ITENS/PAGAMENTOS ANTIGOS
        List<ItemVenda> itensAntigos = itemVendaRepository.findByVenda_IdVenda(id);
        for (ItemVenda itemAntigo : itensAntigos) {
            Estoque estoqueProduto = estoqueRepository.findByProduto_IdProduto(itemAntigo.getProduto().getIdProduto())
                    .stream().findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Estoque", "produto", itemAntigo.getProduto().getNomeProduto()));
            estoqueProduto.setQuantidade(estoqueProduto.getQuantidade() + itemAntigo.getQuantidadeVendida());
            estoqueRepository.save(estoqueProduto);
        }
        itemVendaRepository.deleteAll(itensAntigos); // Remove todos os itens antigos
        vendaExistente.getPagamentos().clear(); // Remove os pagamentos antigos via orphanRemoval (definido na Model Venda)

        // 2. ATUALIZAR DADOS DA VENDA (HEADER)
        if (vendaRequestDTO.getDataVenda() != null && !vendaRequestDTO.getDataVenda().isBlank()) {
            // Ajustado para OffsetDateTime para suportar o formato ISO com timezone (Z) enviado pelo JS
            vendaExistente.setDataVenda(OffsetDateTime.parse(vendaRequestDTO.getDataVenda()).toLocalDateTime());
        } else {
            // Se a data não for fornecida no DTO, mantém a data original da venda
            // ou define como a data atual, dependendo da regra de negócio.
            // Por enquanto, vamos manter a data original se não for explicitamente alterada.
        }

        // Atualiza o usuário responsável pela venda
        if (vendaRequestDTO.getIdUser() != null) {
            Usuario usuario = usuarioRepository.findById(vendaRequestDTO.getIdUser())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", vendaRequestDTO.getIdUser()));
            vendaExistente.setUser(usuario);
        }

        BigDecimal valorTotalCalculado = BigDecimal.ZERO;

        // 3. PROCESSAR NOVOS ITENS DA VENDA
        if (vendaRequestDTO.getItens() != null) {
            for (VendaRequestDTO.ItemVendaRequestDTO itemDTO : vendaRequestDTO.getItens()) {
                Produto produto = produtoRepository.findById(itemDTO.getIdProduto())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto", "id", itemDTO.getIdProduto()));

                // --- BAIXA DE ESTOQUE ---
                List<Estoque> estoques = estoqueRepository.findByProduto_IdProduto(produto.getIdProduto());
                if (estoques.isEmpty()) {
                    throw new ResourceNotFoundException("Estoque", "produto", produto.getNomeProduto());
                }
                Estoque estoque = estoques.get(0);
                if (estoque.getQuantidade() < itemDTO.getQuantidade()) {
                    throw new BadRequestException("Estoque insuficiente para o produto: " + produto.getNomeProduto());
                }
                estoque.setQuantidade(estoque.getQuantidade() - itemDTO.getQuantidade());
                estoqueRepository.save(estoque);

                // --- REGISTRO DO NOVO ITEM DA VENDA ---
                ItemVenda itemVenda = new ItemVenda();
                itemVenda.setVenda(vendaExistente); // Vincula ao objeto de venda existente
                itemVenda.setProduto(produto);
                itemVenda.setQuantidadeVendida(itemDTO.getQuantidade());
                itemVenda.setActive(true);
                itemVendaRepository.save(itemVenda);

                BigDecimal preco = produto.getValorUnitario();
                BigDecimal quantidade = new BigDecimal(itemDTO.getQuantidade());
                valorTotalCalculado = valorTotalCalculado.add(preco.multiply(quantidade));
            }
        }

        // 4. PROCESSAR NOVOS PAGAMENTOS
        processarPagamentosVenda(vendaExistente, vendaRequestDTO.getPagamentos(), valorTotalCalculado);
        vendaExistente.setValorTotal(valorTotalCalculado); // Atualiza o valor total calculado para refletir na edição

        Venda atualizado = this.vendaRepository.save(vendaExistente); // Salva a venda com o novo valor total
        return toResponseDTO(atualizado);
    }

    @Override
    @Transactional
    public void excluirVenda(Long id){
        Venda venda = this.vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda", "id", id));

        venda.setActive(false);
        vendaRepository.save(venda);
    }

    /**
     * Método auxiliar para processar pagamentos, validar o total e persistir os registros.
     */
    private void processarPagamentosVenda(Venda venda, List<PagamentoRequestDTO> pagamentosDTO, BigDecimal valorTotalCalculado) {
        BigDecimal totalPago = BigDecimal.ZERO;
        
        // Garante que a lista de pagamentos na entidade não seja nula
        if (venda.getPagamentos() == null) {
            venda.setPagamentos(new java.util.ArrayList<>());
        }

        if (pagamentosDTO != null) {
            for (PagamentoRequestDTO pagDTO : pagamentosDTO) {
                FormaPagamento tipo;
                try {
                    tipo = FormaPagamento.paraEnum(pagDTO.getFormaPagamento());
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException(e.getMessage());
                }

                if (FormaPagamento.CARTAO_CREDITO.equals(tipo) && pagDTO.getParcelas() > 4) {
                    throw new BadRequestException("O parcelamento no cartão de crédito é permitido em até 4x.");
                }

                PagamentoVenda pag = new PagamentoVenda();
                pag.setVenda(venda);
                pag.setFormaPagamento(tipo);
                pag.setValorPago(pagDTO.getValorPago());
                pag.setParcelas(pagDTO.getParcelas());
                
                venda.getPagamentos().add(pag); // Adiciona à lista para garantir integridade via cascade
                pagamentoVendaRepository.save(pag);
                totalPago = totalPago.add(pagDTO.getValorPago());
            }
        }
        if (totalPago.compareTo(valorTotalCalculado) != 0) {
            throw new BadRequestException("A soma dos pagamentos (" + totalPago + ") deve ser igual ao total da venda (" + valorTotalCalculado + ")");
        }
    }



    
    private VendaResponseDTO toResponseDTO(Venda venda) {
        String resumoPagamento = venda.getPagamentos().stream()
                .map(p -> p.getFormaPagamento().getDescricao())
                .distinct()
                .collect(Collectors.joining(", "));

        return new VendaResponseDTO(
                venda.getIdVenda(),
                resumoPagamento,
                venda.getDataVenda(),
                venda.getValorTotal(),
                venda.isActive(),
                new UsuarioResponseDTO(
                    venda.getUser().getId(), 
                    venda.getUser().getNome(), 
                    venda.getUser().getEmail(), 
                    venda.getUser().getRole(),
                    venda.getUser().isActive()
                )
        );

    }
}
