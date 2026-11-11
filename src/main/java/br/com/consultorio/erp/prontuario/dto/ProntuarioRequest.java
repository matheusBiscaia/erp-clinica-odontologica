package br.com.consultorio.erp.prontuario.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProntuarioRequest(

    @NotNull(message = "O paciente é obrigatório")
    Long pacienteId,

    @NotNull(message = "O dentista é obrigatório")
    Long dentistaId,

    @NotNull(message = "A data do atendimento é obrigatória")
    LocalDateTime dataAtendimento,

    @NotBlank(message = "O diagnóstico é obrigatório")
    @Size(max = 1000, message = "O diagnóstico deve possuir até 1000 caracteres")
    String diagnostico,

    @NotBlank(message = "O tratamento é obrigatório")
    @Size(max = 1000, message = "O tratamento deve possuir até 1000 caracteres")
    String tratamento,

    @Size(max = 2000, message = "As observações devem possuir até 2000 caracteres")
    String observacoes
) {
}