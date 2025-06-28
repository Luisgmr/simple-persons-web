package com.luisgmr.senai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class PessoaResponseDTO {
    private Integer idPessoa;
    private String mensagem;
}