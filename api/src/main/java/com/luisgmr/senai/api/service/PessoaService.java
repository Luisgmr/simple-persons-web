package com.luisgmr.senai.api.service;

import com.luisgmr.senai.api.validation.PessoaValidacao;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luisgmr.senai.api.dto.*;
import com.luisgmr.senai.api.mapper.PessoaMapper;
import com.luisgmr.senai.api.domain.Pessoa;
import com.luisgmr.senai.api.repository.PessoaRepository;

import java.time.LocalDateTime;

@Service
@Transactional
public class PessoaService {
    private final PessoaRepository repository;
    private final PessoaMapper mapper;
    private final PessoaValidacao pessoaValidacao;
    
    public PessoaService(PessoaRepository repository, @Qualifier("pessoaMapperImpl") PessoaMapper mapper, PessoaValidacao pessoaValidacao) {
        this.repository = repository;
        this.mapper = mapper;
        this.pessoaValidacao = pessoaValidacao;
    }

    public PessoaResponseDTO create(PessoaRequestDTO dto) {
        pessoaValidacao.validarDataNascimento(dto.getDataNascimento());
        pessoaValidacao.validarCep(dto.getEndereco().getCep());
        
        if (repository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        Pessoa entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toResponse(entity);
    }

    public PessoaResponseDTO update(String cpf, PessoaRequestDTO dto) {
        pessoaValidacao.validarDataNascimento(dto.getDataNascimento());
        pessoaValidacao.validarCep(dto.getEndereco().getCep());
        
        Pessoa pessoa = repository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada"));
        
        pessoa.setNome(dto.getNome());
        pessoa.setNascimento(dto.getDataNascimento());
        pessoa.setEmail(dto.getEmail());
        pessoa.setAlteracaoRegistro(LocalDateTime.now());
        
        if (dto.getEndereco() != null) {
            if (pessoa.getEndereco() == null) {
                pessoa.setEndereco(mapper.toEntity(dto).getEndereco());
            } else {
                pessoa.getEndereco().setCep(dto.getEndereco().getCep());
                pessoa.getEndereco().setRua(dto.getEndereco().getRua());
                pessoa.getEndereco().setNumero(dto.getEndereco().getNumero());
                pessoa.getEndereco().setCidade(dto.getEndereco().getCidade());
                pessoa.getEndereco().setEstado(dto.getEndereco().getEstado());
            }
        }
        
        return mapper.toResponse(pessoa);
    }

    @Transactional(readOnly = true)
    public PessoaDetailsDTO findByCpf(String cpf) {
        Pessoa pessoa = repository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        return mapper.toDetails(pessoa);
    }

    public MensagemResponseDTO deleteByCpf(String cpf) {
        Pessoa pessoa = repository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        repository.delete(pessoa);
        return new MensagemResponseDTO(pessoa.getNome() + " foi removido(a)");
    }
}
