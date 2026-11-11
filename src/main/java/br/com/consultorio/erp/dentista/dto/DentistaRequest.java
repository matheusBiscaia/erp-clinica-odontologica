package br.com.consultorio.erp.dentista.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DentistaRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150)
        String nomeCompleto,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(
                regexp = "\\d{11}",
                message = "O CPF deve possuir 11 números"
        )
        String cpf,

        @NotBlank(message = "O CRO é obrigatório")
        @Size(max = 20)
        String cro,

        @NotBlank(message = "A especialidade é obrigatória")
        @Size(max = 100)
        String especialidade,

        @Size(max = 20)
        String telefone,

        @Email(message = "O e-mail deve ser válido")
        @Size(max = 150)
        String email
) {
}
