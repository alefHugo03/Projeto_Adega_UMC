package api.servico.adega.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import api.servico.adega.enums.FormaPagamento;
import api.servico.adega.model.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long>{
    /**
     * Procura um usuário pelo email.
     */
    @Override
    Optional<Venda> findById(Long id);

    /**
     * Verifica se já existe usuário com o email informado.
     */
    @Override
    boolean existsById(Long id);

    Page<Venda> findByDataHoraVendaBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    Page<Venda> findByUser_Id(Long idUsuario, Pageable pageable);

    Page<Venda> findDistinctByPagamentos_FormaPagamento(FormaPagamento formaPagamento, Pageable pageable);
}
