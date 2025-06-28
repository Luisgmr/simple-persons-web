package com.luisgmr.senai.backend.controller;

import com.luisgmr.senai.backend.dto.request.CadastrarPessoaRequestDTO;
import com.luisgmr.senai.backend.dto.response.*;
import com.luisgmr.senai.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/pessoa")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PessoaController {
    private final PessoaService service;

    @PostMapping
    public ResponseEntity<CadastrarPessoaResponseDTO> criar(@Valid @RequestBody CadastrarPessoaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarPessoa(dto));
    }

    @PutMapping("/cpf/{cpf}")
    public ResponseEntity<CadastrarPessoaResponseDTO> atualizar(@PathVariable String cpf, @Valid @RequestBody CadastrarPessoaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizarPessoa(cpf, dto));
    }

    @GetMapping
    public ResponseEntity<List<CadastrarPessoaResponseDTO>> listar() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/paginado")
    public ResponseEntity<PaginaResponseDTO<PessoaConsultaResponseDTO>> listarPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho
    ) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        return ResponseEntity.ok(service.findAllPaginated(pageable));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PessoaConsultaResponseDTO> consultar(@PathVariable String cpf) {
        return ResponseEntity.ok(service.consultarPessoa(cpf));
    }

    @PostMapping("/cpf/{cpf}/integrar")
    public ResponseEntity<MensagemResponseDTO> integrar(@PathVariable String cpf) {
        return ResponseEntity.ok(service.integrarPessoa(cpf));
    }

    @GetMapping("/cpf/{cpf}/integrada")
    public ResponseEntity<PessoaIntegradaResponseDTO> consultarIntegrada(@PathVariable String cpf) {
        return ResponseEntity.ok(service.consultarPessoaIntegrada(cpf));
    }

    @DeleteMapping("/cpf/{cpf}")
    public ResponseEntity<MensagemResponseDTO> remover(@PathVariable String cpf) {
        return ResponseEntity.ok(service.deletarPessoa(cpf));
    }
}