package br.com.consultorio.erp.prontuario.dto;

import java.time.LocalDateTime;

public record ProntuarioResponse(
    Long id,
    Long pacienteId,
    String pacienteNome,
    Long dentistaId,
    String dentistaNome,
    LocalDateTime dataAtendimento,
    String diagnostico,
    String tratamento,
    String observacoes
) {
}