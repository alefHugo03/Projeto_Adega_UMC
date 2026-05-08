package api.servico.adega.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.servico.adega.dto.requests.VendaRequestDTO;
import api.servico.adega.dto.responses.UsuarioResponseDTO;
import api.servico.adega.dto.responses.VendaResponseDTO;
import api.servico.adega.exception.ResourceNotFoundException;
import api.servico.adega.model.Usuario;
import api.servico.adega.model.Venda;
import api.servico.adega.repository.UsuarioRepository;
import api.servico.adega.repository.VendaRepository;
import api.servico.adega.service.VendaService;

@Service
@Transactional(readOnly = true)
public class VendaServiceImpl implements VendaService {

    private final VendaRepository vendaRepository;
    private final UsuarioRepository usuarioRepository;

    public VendaServiceImpl(VendaRepository vendaRepository, UsuarioRepository usuarioRepository) {
        this.vendaRepository = vendaRepository;
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public List<VendaResponseDTO> listarVenda() {
        return vendaRepository.findAll()
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
        List<Venda> vendas = vendaRepository.findByFormaPagamento(formaPagamento);

        if (vendas.isEmpty()) {
            throw new ResourceNotFoundException("Venda", "formaPagamento", formaPagamento);
        }

        return vendas.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public VendaResponseDTO criarVenda(VendaRequestDTO vendaRequestDTO){
        Venda venda = toEntity(vendaRequestDTO);
        Venda salvo = vendaRepository.save(venda);
        return toResponseDTO(salvo);
    }

    @Override
    @Transactional
    public VendaResponseDTO editarVenda(Long id, VendaRequestDTO vendaRequestDTO){
        Venda vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda", "id", id));

        vendaExistente.setValorTotal(vendaRequestDTO.getValorTotal());
        vendaExistente.setFormaPagamento(vendaRequestDTO.getFormaPagamento());

        Venda atualizado = vendaRepository.save(vendaExistente);
        return  toResponseDTO(atualizado);
    }

    @Override
    @Transactional
    public void excluirVenda(Long id){
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda", "id", id));

        vendaRepository.delete(venda);
    }




    
    private VendaResponseDTO toResponseDTO(Venda venda) {
        return new VendaResponseDTO(
                venda.getIdVenda(),
                venda.getFormaPagamento(),
                venda.getDataVenda(),
                venda.getValorTotal(),
                new UsuarioResponseDTO(venda.getUser().getId(), venda.getUser().getNome(), venda.getUser().getEmail())
        );

    }

    private Venda toEntity(VendaRequestDTO dto) {
        Venda venda = new Venda();
        venda.setFormaPagamento(dto.getFormaPagamento());
        venda.setValorTotal(dto.getValorTotal());
        venda.setDataVenda(LocalDateTime.parse(dto.getDataVenda()));
        Usuario usuario = usuarioRepository.findById(dto.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", dto.getIdUser()));
        venda.setUser(usuario);
        return venda;
    }
}
