package com.luisgmr.senai.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErroResponseDTO {
    private String timestamp;
    private String erro;
    private String mensagem;
    private String caminho;
}
