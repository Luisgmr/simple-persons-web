package com.luisgmr.senai.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Data
public class PessoaRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @CPF(message = "CPF inválido")
    private String cpf;

    @Email(message = "Email inválido")
    private String email;

    @Valid
    private EnderecoDTO endereco;

    @Data
    public static class EnderecoDTO {
        @NotBlank(message = "CEP é obrigatório") private String cep;
        @NotBlank(message = "Rua é obrigatória") private String rua;
        @NotBlank(message = "Número é obrigatório") private String numero;
        @NotBlank(message = "Cidade é obrigatória") private String cidade;
        @NotBlank(message = "Estado é obrigatório") private String estado;
    }
}
