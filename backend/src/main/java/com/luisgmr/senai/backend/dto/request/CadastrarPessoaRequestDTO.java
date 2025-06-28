package com.luisgmr.senai.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CadastrarPessoaRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private LocalDate dataNascimento;

    private String cpf;

    private String email;

    private EnderecoDTO endereco;

    @Data
    public static class EnderecoDTO {
        private String cep;
        private String rua;
        private Integer numero;
        private String cidade;
        private String estado;
    }
}
