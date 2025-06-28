package com.luisgmr.senai.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class PessoaIntegradaDTO {
    private String nome;
    private LocalDate dataNascimento;
    private String situacaoIntegracao;
    private LocalDateTime dataHoraInclusao;
    private LocalDateTime dataHoraUltimaAlteracao;
}