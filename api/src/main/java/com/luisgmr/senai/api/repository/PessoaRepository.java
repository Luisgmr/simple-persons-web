package com.luisgmr.senai.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.luisgmr.senai.api.domain.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    Optional<Pessoa> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
