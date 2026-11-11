package br.com.consultorio.erp.agendamento.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.consultorio.erp.agendamento.entity.Agendamento;
import br.com.consultorio.erp.agendamento.entity.StatusAgendamento;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, Long> {

    boolean existsByDentistaIdAndDataHoraAndStatusNot(
            Long dentistaId,
            LocalDateTime dataHora,
            StatusAgendamento status
    );

    boolean existsByDentistaIdAndDataHoraAndStatusNotAndIdNot(
            Long dentistaId,
            LocalDateTime dataHora,
            StatusAgendamento status,
            Long id
    );

    List<Agendamento> findAllByOrderByDataHoraAsc();
}