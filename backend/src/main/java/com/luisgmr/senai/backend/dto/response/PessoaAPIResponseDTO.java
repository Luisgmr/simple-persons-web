package com.luisgmr.senai.backend.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PessoaAPIResponseDTO {
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String email;
    private EnderecoDTO endereco;
    private LocalDateTime dataHoraInclusaoRegistro;
    private LocalDateTime dataHoraUltimaAlteracaoRegistro;

    @Data
    public static class EnderecoDTO {
        private String cep;
        private String rua;
        private String numero;
        private String cidade;
        private String estado;
    }
}