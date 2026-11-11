package br.com.consultorio.erp.agendamento.dto;

import java.time.LocalDateTime;

import br.com.consultorio.erp.agendamento.entity.StatusAgendamento;

public record AgendamentoResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        Long dentistaId,
        String dentistaNome,
        LocalDateTime dataHora,
        String procedimento,
        String observacoes,
        StatusAgendamento status
) {
}