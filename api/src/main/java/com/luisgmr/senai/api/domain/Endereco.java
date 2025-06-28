package com.luisgmr.senai.api.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "cidade", length = 60)
    private String cidade;

    @Column(name = "estado", length = 60)
    private String estado;
}