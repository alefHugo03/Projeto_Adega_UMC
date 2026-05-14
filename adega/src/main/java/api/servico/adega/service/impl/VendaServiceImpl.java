package api.servico.adega.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public VendaResponseDTO buscarPorId(Long id){
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("venda", "id", id));
        return toResponseDTO(venda);
    }

    @Override
    public List<VendaResponseDTO> buscarPorData(String data) {
        LocalDate dataConvertida = LocalDate.parse(data);
        LocalDateTime inicio = dataConvertida.atStartOfDay();
        LocalDateTime fim = dataConvertida.atTime(23, 59, 59, 999_999_999);
        List<Venda> vendas = vendaRepository.findByDataVendaBetween(inicio, fim);

        if (vendas.isEmpty()) {
            throw new ResourceNotFoundException("Venda", "data", data);
        }

        return vendas.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<VendaResponseDTO> buscarPorIdUsuario(Long id){
        List<Venda> vendas = vendaRepository.findByUser_Id(id);
        return vendas.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    
    @Override
    public List<VendaResponseDTO> buscarPorFormaPagamento(String formaPagamento){
        FormaPagamento tipo;
        try {
            tipo = FormaPagamento.paraEnum(formaPagamento);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Forma de pagamento inválida: " + formaPagamento);
        }

        List<Venda> vendas = vendaRepository.findDistinctByPagamentos_FormaPagamento(tipo);

        if (vendas.isEmpty()) {
            throw new ResourceNotFoundException("Venda", "formaPagamento", formaPagamento);
        }

        return vendas.stream().map(this::toResponseDTO).toList();
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
        BigDecimal totalPago = BigDecimal.ZERO;
        if (vendaRequestDTO.getPagamentos() != null) {
            for (PagamentoRequestDTO pagDTO : vendaRequestDTO.getPagamentos()) {
                FormaPagamento tipo;
                try {
                    tipo = FormaPagamento.paraEnum(pagDTO.getFormaPagamento());
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException(e.getMessage());
                }

                // Validação de Parcelas (Máximo 4x para Crédito)
                if (FormaPagamento.CARTAO_CREDITO.equals(tipo) && pagDTO.getParcelas() > 4) {
                    throw new BadRequestException("O parcelamento no cartão de crédito é permitido em até 4x.");
                }

                PagamentoVenda pag = new PagamentoVenda();
                pag.setVenda(venda);
                pag.setFormaPagamento(tipo);
                pag.setValorPago(pagDTO.getValorPago());
                pag.setParcelas(pagDTO.getParcelas());
                
                pagamentoVendaRepository.save(pag);
                totalPago = totalPago.add(pagDTO.getValorPago());
            }
        }

        // Valida se o valor total pago condiz com o total da venda
        if (totalPago.compareTo(valorTotalCalculado) != 0) {
            throw new BadRequestException("A soma dos pagamentos (" + totalPago + ") deve ser igual ao total da venda (" + valorTotalCalculado + ")");
        }

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

        // Exemplo de atualização de data se fornecida
        if (vendaRequestDTO.getDataVenda() != null && !vendaRequestDTO.getDataVenda().isBlank()) {
            vendaExistente.setDataVenda(LocalDateTime.parse(vendaRequestDTO.getDataVenda()));
        }

        // Nota: Atualizar Itens e Pagamentos em uma venda existente exige 
        // lógica para estornar estoque e substituir registros antigos.

        Venda atualizado = this.vendaRepository.save(vendaExistente);
        return  toResponseDTO(atualizado);
    }

    @Override
    @Transactional
    public void excluirVenda(Long id){
        Venda venda = this.vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda", "id", id));

        venda.setActive(false);
        vendaRepository.save(venda);
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
                new UsuarioResponseDTO(venda.getUser().getId(), venda.getUser().getNome(), venda.getUser().getEmail())
        );

    }
}
