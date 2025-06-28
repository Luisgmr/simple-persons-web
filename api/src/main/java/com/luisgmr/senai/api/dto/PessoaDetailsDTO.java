package com.luisgmr.senai.api.dto;

import lombok.*;
import java.time.*;

@Data
public class PessoaDetailsDTO {
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