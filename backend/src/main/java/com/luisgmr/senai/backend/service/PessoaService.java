package com.luisgmr.senai.backend.service;

import com.luisgmr.senai.backend.domain.*;
import com.luisgmr.senai.backend.dto.request.CadastrarPessoaRequestDTO;
import com.luisgmr.senai.backend.dto.request.IntegrarPessoaRequestDTO;
import com.luisgmr.senai.backend.dto.response.*;
import com.luisgmr.senai.backend.exception.IntegracaoPessoaException;
import com.luisgmr.senai.backend.mapper.*;
import com.luisgmr.senai.backend.messaging.producer.PessoaProducer;
import com.luisgmr.senai.backend.repository.*;
import com.luisgmr.senai.backend.validation.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.client.HttpClientErrorException;
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

    public CadastrarPessoaResponseDTO criarPessoa(CadastrarPessoaRequestDTO dto) {
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
            pessoaProducer.enviarParaFila(
                    IntegrarPessoaRequestDTO.builder()
                            .pessoaConsultaResponseDTO(mapper.toDetails(pessoa))
                            .isBotaoIntegrar(false)
                            .isCpfAlterado(false)
                            .build()
            );
        }

        return mapper.toResponse(pessoa);
    }

    public CadastrarPessoaResponseDTO atualizarPessoa(Integer id, CadastrarPessoaRequestDTO dto) {
        validarCampos(dto);

        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada"));
        
        String cpfAnterior = pessoa.getCpf();
        boolean cpfAlterado = cpfAnterior != null && !cpfAnterior.equals(dto.getCpf());
        
        pessoa.setNome(dto.getNome());
        pessoa.setNascimento(dto.getDataNascimento());
        pessoa.setEmail(dto.getEmail());
        pessoa.setCpf(dto.getCpf());

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

        if (cpfAlterado && pessoa.getSituacaoIntegracao() != SituacaoIntegracao.NAO_ENVIADO)
            deletarPessoaAPI(cpfAnterior);

        if (pessoaValidacao.isCamposPreenchidos(pessoa)) {
            pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE);
        } else {
            pessoa.setSituacaoIntegracao(SituacaoIntegracao.NAO_ENVIADO);
        }

        repository.save(pessoa);

        return mapper.toResponse(pessoa);
    }

    @Transactional(readOnly = true)
    public List<CadastrarPessoaResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaginaResponseDTO<PessoaConsultaResponseDTO> findAllPaginated(Pageable pageable) {
        Page<Pessoa> result = repository.findAll(pageable);

        return new PaginaResponseDTO<>(
                result.stream().map(mapper::toDetails).toList(),
                result.getNumber(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public PessoaConsultaResponseDTO consultarPessoa(String cpf) {
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

        if (pessoaValidacao.isCamposPreenchidos(pessoa)) {
            validarCampos(mapper.toRequest(pessoa));
        } else {
            throw new IntegracaoPessoaException("Todos os dados da pessoa devem ser preenchidos para realizar uma integração");
        }

        boolean isCpfAlterado = !existePessoaNaAPI(cpf);

        pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE);
        repository.save(pessoa);
        pessoaProducer.enviarParaFila(
                IntegrarPessoaRequestDTO.builder()
                        .pessoaConsultaResponseDTO(mapper.toDetails(pessoa))
                        .isBotaoIntegrar(true)
                        .isCpfAlterado(isCpfAlterado)
                        .build()
        );
        return new MensagemResponseDTO("Enviando pessoa para a fila de integração");
    }

    private boolean existePessoaNaAPI(String cpf) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(apiUrl + "/pessoa/cpf/" + cpf, PessoaAPIResponseDTO.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public PessoaIntegradaResponseDTO consultarPessoaIntegrada(String cpf) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Pessoa pessoa = repository.findByCpf(cpf)
                    .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
            PessoaAPIResponseDTO apiDto = restTemplate.getForObject(apiUrl + "/pessoa/cpf/" + cpf, PessoaAPIResponseDTO.class);
            assert apiDto != null;
            return PessoaIntegradaResponseDTO.builder()
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

    public void deletarPessoaAPI(String cpf) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.delete(apiUrl + "/pessoa/cpf/" + cpf);
        } catch (Exception e) {
            throw new EntityNotFoundException("Pessoa não encontrada na API");
        }
    }

    public MensagemResponseDTO deletarPessoa(Integer id) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            if (pessoa.getCpf() != null && !pessoa.getCpf().isBlank())
                restTemplate.delete(apiUrl + "/pessoa/cpf/" + pessoa.getCpf());
            repository.delete(pessoa);
            return new MensagemResponseDTO(pessoa.getNome() + " foi removido(a)");
        } catch (EntityNotFoundException e) {
            repository.delete(pessoa);
            return new MensagemResponseDTO(pessoa.getNome() + " foi removido(a)");
        } catch (HttpClientErrorException e) {
            ErroResponseDTO erro = e.getResponseBodyAs(ErroResponseDTO.class);
            if (erro != null && erro.getErro().equals(EntityNotFoundException.class.getSimpleName())) {
                throw new IntegracaoPessoaException("A pessoa não corresponde aos dados presentes na API. Realize a integração e tente novamente.");
            }
        } catch (Exception e) {
            throw new IntegracaoPessoaException("Não foi possível remover a pessoa da API. Remoção cancelada.");
        }
        return null;
    }

    private void validarCampos(CadastrarPessoaRequestDTO dto) {
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