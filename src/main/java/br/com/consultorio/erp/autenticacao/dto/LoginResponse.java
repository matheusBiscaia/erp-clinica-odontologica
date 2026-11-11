package br.com.consultorio.erp.autenticacao.dto;

public record LoginResponse(
    String tipo,
    String token,
    Long usuarioId,
    String nome,
    String email,
    String perfil
) {
}