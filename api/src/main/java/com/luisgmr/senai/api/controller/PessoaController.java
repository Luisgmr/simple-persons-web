package com.luisgmr.senai.api.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.luisgmr.senai.api.dto.*;
import com.luisgmr.senai.api.service.PessoaService;

@RestController
@RequestMapping("/pessoa")
@RequiredArgsConstructor
public class PessoaController {
    private final PessoaService service;

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> criar(@Valid @RequestBody PessoaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaResponseDTO> atualizar(@PathVariable String cpf, @Valid @RequestBody PessoaRequestDTO dto) {
        return ResponseEntity.ok(service.update(cpf, dto));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaDetailsDTO> consultar(@PathVariable String cpf) {
        return ResponseEntity.ok(service.findByCpf(cpf));
    }

    @DeleteMapping("/cpf/{cpf}")
    public ResponseEntity<MensagemResponseDTO> remover(@PathVariable String cpf) {
        return ResponseEntity.ok(service.deleteByCpf(cpf));
    }
}