package br.com.consultorio.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PagamentoRequest(

    @NotNull(message = "O paciente é obrigatório")
    Long pacienteId,

    Long agendamentoId,

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 200, message = "A descrição deve possuir até 200 caracteres")
    String descricao,

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(
        value = "0.01",
        message = "O valor deve ser maior que zero"
    )
    BigDecimal valor,

    @NotNull(message = "A data de vencimento é obrigatória")
    LocalDate dataVencimento
) {
}