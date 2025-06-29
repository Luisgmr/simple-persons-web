package com.luisgmr.senai.api.service;

import com.luisgmr.senai.api.domain.Endereco;
import com.luisgmr.senai.api.domain.Pessoa;
import com.luisgmr.senai.api.dto.PessoaRequestDTO;
import com.luisgmr.senai.api.mapper.PessoaMapper;
import com.luisgmr.senai.api.repository.PessoaRepository;
import com.luisgmr.senai.api.testutil.Generator;
import com.luisgmr.senai.api.validation.PessoaValidacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PessoaServiceTest {

    @Mock PessoaRepository repository;
    @Mock PessoaMapper mapper;
    @Mock PessoaValidacao pessoaValidacao;

    @InjectMocks PessoaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Criar pessoa com sucesso")
    void create_success() {
        PessoaRequestDTO dto = new PessoaRequestDTO();
        dto.setNome("João Silva");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setCpf("1240589898");
        
        PessoaRequestDTO.EnderecoDTO endereco = new PessoaRequestDTO.EnderecoDTO();
        endereco.setCep("88701000");
        endereco.setRua("Rua Teste");
        endereco.setNumero(123);
        endereco.setCidade("Tubarão");
        endereco.setEstado("SC");
        dto.setEndereco(endereco);

        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");

        when(repository.existsByCpf("12345678901")).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(pessoa);

        service.create(dto);

        verify(repository).save(pessoa);
    }

    @Test
    @DisplayName("Erro ao criar pessoa com CPF duplicado")
    void create_duplicateCpf() {
        PessoaRequestDTO dto = new PessoaRequestDTO();
        dto.setCpf("12345678901");
        dto.setNome("João");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setEndereco(createEnderecoDTO());

        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");
    }

    @Test
    @DisplayName("Atualizar pessoa com sucesso")
    void update_success() {
        String cpf = "12345678901";
        PessoaRequestDTO dto = new PessoaRequestDTO();
        dto.setNome("João Silva Atualizado");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setEndereco(createEnderecoDTO());

        Pessoa pessoa = new Pessoa();
        pessoa.setCpf(cpf);
        pessoa.setNome("João Silva");
        
        Pessoa pessoaComEndereco = new Pessoa();
        Endereco enderecoEntity = new Endereco();
        pessoaComEndereco.setEndereco(enderecoEntity);
        
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(pessoa));
        when(mapper.toEntity(dto)).thenReturn(pessoaComEndereco);

        service.update(cpf, dto);

        assertThat(pessoa.getNome()).isEqualTo("João Silva Atualizado");
    }

    public PessoaRequestDTO.EnderecoDTO createEnderecoDTO() {
        PessoaRequestDTO.EnderecoDTO endereco = new PessoaRequestDTO.EnderecoDTO();
        endereco.setCep("88701000");
        endereco.setRua("Rua Teste");
        endereco.setNumero(123);
        endereco.setCidade("Tubarão");
        endereco.setEstado("SC");
        return endereco;
    }

}