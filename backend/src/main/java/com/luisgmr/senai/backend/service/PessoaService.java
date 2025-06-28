package com.luisgmr.senai.backend.service;

import com.luisgmr.senai.backend.domain.*;
import com.luisgmr.senai.backend.dto.*;
import com.luisgmr.senai.backend.exception.IntegracaoPessoaException;
import com.luisgmr.senai.backend.mapper.*;
import com.luisgmr.senai.backend.messaging.producer.PessoaProducer;
import com.luisgmr.senai.backend.repository.*;
import com.luisgmr.senai.backend.validation.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class PessoaService {
    private final PessoaRepository repository;
    private final PessoaMapper mapper;
    private final PessoaValidacao pessoaValidacao;
    private final PessoaProducer pessoaProducer;

    @Value("${api.pessoa.url:http://localhost:8081}")
    private String apiUrl;

    public PessoaResponseDTO criarPessoa(PessoaRequestDTO dto) {
        validarCampos(dto);

        if (dto.getCpf() != null && repository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        Pessoa pessoa = mapper.toEntity(dto);
        repository.save(pessoa);

        if (pessoaValidacao.isCamposPreenchidos(
            dto.getNome(), dto.getDataNascimento(), dto.getCpf(), dto.getEmail(),
            dto.getEndereco() != null ? dto.getEndereco().getCep() : null,
            dto.getEndereco() != null ? dto.getEndereco().getRua() : null,
            dto.getEndereco() != null ? dto.getEndereco().getNumero() : null,
            dto.getEndereco() != null ? dto.getEndereco().getCidade() : null,
            dto.getEndereco() != null ? dto.getEndereco().getEstado() : null)
        ) {
            pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE);
            pessoaProducer.enviarParaFila(mapper.toDetails(pessoa));
        }

        return mapper.toResponse(pessoa);
    }

    public PessoaResponseDTO atualizarPessoa(String cpf, PessoaRequestDTO dto) {
        validarCampos(dto);

        Pessoa pessoa = repository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada"));
        
        pessoa.setNome(dto.getNome());
        pessoa.setNascimento(dto.getDataNascimento());
        pessoa.setEmail(dto.getEmail());

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

        pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE);

        repository.save(pessoa);

        return mapper.toResponse(pessoa);
    }

    @Transactional(readOnly = true)
    public List<PessoaResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PessoaDetailsDTO consultarPessoa(String cpf) {
        Pessoa pessoa = repository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        return mapper.toDetails(pessoa);
    }

    public MensagemResponseDTO integrarPessoa(String cpf) {
        Pessoa pessoa = repository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));

        if (pessoa.getSituacaoIntegracao() != SituacaoIntegracao.PENDENTE && pessoa.getSituacaoIntegracao() != SituacaoIntegracao.ERRO) {
            throw new IntegracaoPessoaException("A situação da integração deve ser Pendente ou Erro para realizar uma nova integração");
        }

        if (pessoaValidacao.isCamposPreenchidos(
                pessoa.getNome(),
                pessoa.getNascimento(),
                pessoa.getCpf(),
                pessoa.getEmail(),
                pessoa.getEndereco().getCep(),
                pessoa.getEndereco().getRua(),
                pessoa.getEndereco().getNumero(),
                pessoa.getEndereco().getCidade(),
                pessoa.getEndereco().getEstado()
        )) {
            validarCampos(mapper.toRequest(pessoa));
        } else {
            throw new IntegracaoPessoaException("Todos os dados da pessoa devem ser preenchidos para realizar uma integração");
        }

        pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE);
        repository.save(pessoa);
        pessoaProducer.enviarParaFila(mapper.toDetails(pessoa));
        return new MensagemResponseDTO("Enviando pessoa com o CPF " + cpf + " para a fila de integração");
    }

    @Transactional(readOnly = true)
    public PessoaIntegradaDTO consultarPessoaIntegrada(String cpf) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Pessoa pessoa = repository.findByCpf(cpf)
                    .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
            PessoaApiDTO apiDto = restTemplate.getForObject(apiUrl + "/pessoa/cpf/" + cpf, PessoaApiDTO.class);
            assert apiDto != null;
            return PessoaIntegradaDTO.builder()
                    .nome(apiDto.getNome())
                    .dataNascimento(apiDto.getDataNascimento())
                    .dataHoraInclusao(apiDto.getDataHoraInclusaoRegistro())
                    .dataHoraUltimaAlteracao(apiDto.getDataHoraUltimaAlteracaoRegistro())
                    .situacaoIntegracao(pessoa.getSituacaoIntegracao().toString())
                    .build();
        } catch (Exception e) {
            throw new EntityNotFoundException("Pessoa integrada não encontrada");
        }
    }

    public MensagemResponseDTO deletarPessoa(String cpf) {
        Pessoa pessoa = repository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.delete(apiUrl + "/pessoa/cpf/" + cpf);
            repository.delete(pessoa);
            return new MensagemResponseDTO(pessoa.getNome() + " foi removido(a)");
        } catch (Exception e) {
            throw new IntegracaoPessoaException("Não foi possível remover a pessoa da API. Remoção cancelada.");
        }
    }

    private void validarCampos(PessoaRequestDTO dto) {
        pessoaValidacao.validarNome(dto.getNome());
        pessoaValidacao.validarDataNascimento(dto.getDataNascimento());
        pessoaValidacao.validarCpf(dto.getCpf());
        pessoaValidacao.validarEmail(dto.getEmail());

        if (dto.getEndereco() != null) {
            pessoaValidacao.validarEnderecoCompleto(
                    dto.getEndereco().getCep(),
                    dto.getEndereco().getRua(),
                    dto.getEndereco().getNumero(),
                    dto.getEndereco().getCidade(),
                    dto.getEndereco().getEstado()
            );
        }
    }
}