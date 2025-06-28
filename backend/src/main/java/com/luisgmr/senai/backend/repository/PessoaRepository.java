package com.luisgmr.senai.backend.repository;

import com.luisgmr.senai.backend.domain.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    Optional<Pessoa> findByCpf(@Param("cpf") String cpf);
    List<Pessoa> findAll();
    Page<Pessoa> findAll(Pageable pageable);
    boolean existsByCpf(String cpf);
}
