package br.com.consultorio.erp.financeiro.dto;

import br.com.consultorio.erp.financeiro.entity.FormaPagamento;
import jakarta.validation.constraints.NotNull;

public record PagamentoBaixaRequest(

    @NotNull(message = "A forma de pagamento é obrigatória")
    FormaPagamento formaPagamento
) {
}