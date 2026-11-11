package br.com.consultorio.erp.paciente.dto;

import java.time.LocalDate;

public record PacienteResponse(
    Long id,
    String nomeCompleto,
    String cpf,
    LocalDate dataNascimento,
    String telefone,
    String email,
    Boolean ativo
) {
}