package com.luisgmr.senai.api.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "pessoa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pessoa")
    private Integer idPessoa;

    @Column(name = "criacao_registro", nullable = false, updatable = false)
    private LocalDateTime criacaoRegistro = LocalDateTime.now();

    @Column(name = "alteracao_registro")
    private LocalDateTime alteracaoRegistro;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "nascimento")
    private LocalDate nascimento;

    @Column(name = "cpf", unique = true, length = 11)
    private String cpf;

    @Column(name = "email", length = 120)
    private String email;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Endereco endereco;

    public void setEndereco(Endereco endereco) {
        endereco.setPessoa(this);
        this.endereco = endereco;
    }
}
