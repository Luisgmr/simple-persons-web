package com.luisgmr.senai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CadastrarPessoaResponseDTO {
    private Integer idPessoa;
    private String mensagem;
}