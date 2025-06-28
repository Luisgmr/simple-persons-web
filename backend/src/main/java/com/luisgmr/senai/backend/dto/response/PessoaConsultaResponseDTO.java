package com.luisgmr.senai.backend.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PessoaConsultaResponseDTO {
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String email;
    private String situacaoIntegracao;
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