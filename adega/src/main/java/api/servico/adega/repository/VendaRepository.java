package api.servico.adega.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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

    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Venda> findByUser_Id(Long idUsuario);

    List<Venda> findByFormaPagamento(String formaPagamento);
}
