package br.com.consultorio.erp.dentista.dto;

public record DentistaResponse(
        Long id,
        String nomeCompleto,
        String cpf,
        String cro,
        String especialidade,
        String telefone,
        String email,
        boolean ativo
) {
}
