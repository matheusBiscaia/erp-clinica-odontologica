package br.com.consultorio.erp.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.consultorio.erp.financeiro.entity.FormaPagamento;
import br.com.consultorio.erp.financeiro.entity.StatusPagamento;

public record PagamentoResponse(
    Long id,
    Long pacienteId,
    String pacienteNome,
    Long agendamentoId,
    String descricao,
    BigDecimal valor,
    LocalDate dataVencimento,
    LocalDateTime dataPagamento,
    StatusPagamento status,
    FormaPagamento formaPagamento
) {
}