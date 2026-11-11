package br.com.consultorio.erp.agendamento.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgendamentoRequest(

        @NotNull(message = "O paciente é obrigatório")
        Long pacienteId,

        @NotNull(message = "O dentista é obrigatório")
        Long dentistaId,

        @NotNull(message = "A data e o horário são obrigatórios")
        @FutureOrPresent(
                message = "A data do agendamento não pode estar no passado"
        )
        LocalDateTime dataHora,

        @NotBlank(message = "O procedimento é obrigatório")
        @Size(max = 150)
        String procedimento,

        @Size(max = 500)
        String observacoes
) {
}