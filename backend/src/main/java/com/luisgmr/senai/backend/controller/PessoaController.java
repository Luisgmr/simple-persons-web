package com.luisgmr.senai.backend.controller;

import com.luisgmr.senai.backend.dto.*;
import com.luisgmr.senai.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoa")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PessoaController {
    private final PessoaService service;

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> criar(@Valid @RequestBody PessoaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarPessoa(dto));
    }

    @PutMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaResponseDTO> atualizar(@PathVariable String cpf, @Valid @RequestBody PessoaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizarPessoa(cpf, dto));
    }

    @GetMapping
    public ResponseEntity<List<PessoaResponseDTO>> listar() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaDetailsDTO> consultar(@PathVariable String cpf) {
        return ResponseEntity.ok(service.consultarPessoa(cpf));
    }

    @PostMapping("/cpf/{cpf}/integrar")
    public ResponseEntity<MensagemResponseDTO> integrar(@PathVariable String cpf) {
        return ResponseEntity.ok(service.integrarPessoa(cpf));
    }

    @GetMapping("/cpf/{cpf}/integrada")
    public ResponseEntity<PessoaIntegradaDTO> consultarIntegrada(@PathVariable String cpf) {
        return ResponseEntity.ok(service.consultarPessoaIntegrada(cpf));
    }

    @DeleteMapping("/cpf/{cpf}")
    public ResponseEntity<MensagemResponseDTO> remover(@PathVariable String cpf) {
        return ResponseEntity.ok(service.deletarPessoa(cpf));
    }
}