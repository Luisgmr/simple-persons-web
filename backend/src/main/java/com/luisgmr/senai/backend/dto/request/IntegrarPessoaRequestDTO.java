package com.luisgmr.senai.backend.dto.request;

import com.luisgmr.senai.backend.dto.response.PessoaConsultaResponseDTO;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IntegrarPessoaRequestDTO {
    PessoaConsultaResponseDTO pessoaConsultaResponseDTO;
    boolean isBotaoIntegrar;
    boolean isCpfAlterado;
    String cpfAnterior;
}
