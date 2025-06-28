package com.luisgmr.senai.backend.repository;

import com.luisgmr.senai.backend.domain.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    Optional<Pessoa> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
