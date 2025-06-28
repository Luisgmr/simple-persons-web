package com.luisgmr.senai.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class PessoaIntegradaResponseDTO {
    private String nome;
    private LocalDate dataNascimento;
    private String situacaoIntegracao;
    private LocalDateTime dataHoraInclusao;
    private LocalDateTime dataHoraUltimaAlteracao;
}