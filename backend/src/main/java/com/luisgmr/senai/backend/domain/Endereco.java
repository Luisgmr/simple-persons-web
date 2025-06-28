package com.luisgmr.senai.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "endereco")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Endereco {
    @Id
    @Column(name = "id_pessoa")
    private Integer idPessoa;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa")
    private Pessoa pessoa;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Column(name = "rua", length = 120)
    private String rua;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "cidade", length = 60)
    private String cidade;

    @Column(name = "estado", length = 60)
    private String estado;
}