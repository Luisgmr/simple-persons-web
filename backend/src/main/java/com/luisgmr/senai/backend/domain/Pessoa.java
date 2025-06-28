package com.luisgmr.senai.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "pessoa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pessoa")
    private Integer idPessoa;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "nascimento")
    private LocalDate nascimento;

    @Column(name = "cpf", unique = true, length = 11)
    private String cpf;

    @Column(name = "email", length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_integracao", nullable = false, length = 12)
    private SituacaoIntegracao situacaoIntegracao = SituacaoIntegracao.NAO_ENVIADO;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Endereco endereco;

    public void setEndereco(Endereco endereco) {
        endereco.setPessoa(this);
        this.endereco = endereco;
    }
}