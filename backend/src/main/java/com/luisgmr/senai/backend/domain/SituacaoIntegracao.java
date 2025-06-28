package com.luisgmr.senai.backend.domain;

public enum SituacaoIntegracao {
    NAO_ENVIADO("Não enviado"),
    PENDENTE("Pendente"),
    SUCESSO("Sucesso"),
    ERRO("Erro");

    final String string;

    SituacaoIntegracao(String str) {
        this.string = str;
    }

    @Override
    public String toString() {
        return string;
    }
}
