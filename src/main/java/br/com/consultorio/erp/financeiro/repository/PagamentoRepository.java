package br.com.consultorio.erp.financeiro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.consultorio.erp.financeiro.entity.Pagamento;
import br.com.consultorio.erp.financeiro.entity.StatusPagamento;

public interface PagamentoRepository
        extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findAllByOrderByDataVencimentoDesc();

    List<Pagamento> findByPacienteIdOrderByDataVencimentoDesc(
            Long pacienteId
    );

    List<Pagamento> findByStatusOrderByDataVencimentoAsc(
            StatusPagamento status
    );

    boolean existsByAgendamentoIdAndStatusNot(
            Long agendamentoId,
            StatusPagamento status
    );
}