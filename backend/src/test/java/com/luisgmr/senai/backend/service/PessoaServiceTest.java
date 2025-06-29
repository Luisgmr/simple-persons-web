package com.luisgmr.senai.backend.service;

import com.luisgmr.senai.backend.domain.Pessoa;
import com.luisgmr.senai.backend.domain.SituacaoIntegracao;
import com.luisgmr.senai.backend.dto.request.CadastrarPessoaRequestDTO;
import com.luisgmr.senai.backend.mapper.PessoaMapper;
import com.luisgmr.senai.backend.messaging.producer.PessoaProducer;
import com.luisgmr.senai.backend.repository.PessoaRepository;
import com.luisgmr.senai.backend.validation.PessoaValidacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PessoaServiceTest {

    @Mock PessoaRepository repository;
    @Mock PessoaMapper mapper;
    @Mock PessoaValidacao pessoaValidacao;
    @Mock PessoaProducer pessoaProducer;

    @InjectMocks PessoaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Criar pessoa com todos os dados deve marcar como PENDENTE")
    void criarPessoa_todosOsDados_pendente() {
        CadastrarPessoaRequestDTO dto = new CadastrarPessoaRequestDTO();
        dto.setNome("João Silva");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setCpf("12345678901");
        dto.setEmail("joao@email.com");

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setSituacaoIntegracao(SituacaoIntegracao.NAO_ENVIADO);

        when(repository.existsByCpf("12345678901")).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(pessoa);
        when(pessoaValidacao.isCamposPreenchidos(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(true);

        service.criarPessoa(dto);

        verify(repository).save(pessoa);
        assertThat(pessoa.getSituacaoIntegracao()).isEqualTo(SituacaoIntegracao.PENDENTE);
        verify(pessoaProducer).enviarParaFila(any());
    }

    @Test
    @DisplayName("Criar pessoa com CPF duplicado deve falhar")
    void criarPessoa_cpfDuplicado() {
        CadastrarPessoaRequestDTO dto = new CadastrarPessoaRequestDTO();
        dto.setCpf("12345678901");

        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> service.criarPessoa(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");
    }
}