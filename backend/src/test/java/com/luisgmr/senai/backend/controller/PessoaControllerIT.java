package com.luisgmr.senai.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisgmr.senai.backend.TestcontainersConfiguration;
import com.luisgmr.senai.backend.domain.Pessoa;
import com.luisgmr.senai.backend.domain.SituacaoIntegracao;
import com.luisgmr.senai.backend.dto.request.CadastrarPessoaRequestDTO;
import com.luisgmr.senai.backend.repository.PessoaRepository;
import com.luisgmr.senai.backend.testutil.Generator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfiguration.class)
class PessoaControllerIT {

    @Autowired MockMvc mvc;
    @Autowired PessoaRepository pessoaRepository;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("Criar pessoa com sucesso")
    void criarPessoa() throws Exception {
        CadastrarPessoaRequestDTO dto = new CadastrarPessoaRequestDTO();
        dto.setNome("João Silva");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setCpf(Generator.randomCpf());
        dto.setEmail("joao@email.com");
        
        CadastrarPessoaRequestDTO.EnderecoDTO endereco = new CadastrarPessoaRequestDTO.EnderecoDTO();
        endereco.setCep("88701000");
        endereco.setRua("Rua Teste");
        endereco.setNumero(123);
        endereco.setCidade("Tubarão");
        endereco.setEstado("SC");
        dto.setEndereco(endereco);

        mvc.perform(post("/pessoa")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensagem").value("Operação realizada com sucesso"));
    }

    @Test
    @DisplayName("Erro ao criar pessoa com CPF duplicado")
    void criarPessoa_cpfDuplicado() throws Exception {
        String cpf = Generator.randomCpf();
        
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria");
        pessoa.setCpf(cpf);
        pessoa.setSituacaoIntegracao(SituacaoIntegracao.NAO_ENVIADO);
        pessoaRepository.save(pessoa);

        CadastrarPessoaRequestDTO dto = new CadastrarPessoaRequestDTO();
        dto.setNome("João Silva");
        dto.setCpf(cpf);

        mvc.perform(post("/pessoa")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Listar pessoas paginado")
    void listarPessoas() throws Exception {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Test");
        pessoa.setCpf(Generator.randomCpf());
        pessoa.setSituacaoIntegracao(SituacaoIntegracao.NAO_ENVIADO);
        pessoaRepository.save(pessoa);

        mvc.perform(get("/pessoa/paginado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Integrar pessoa por CPF")
    void integrarPessoa() throws Exception {
        String cpf = Generator.randomCpf();
        
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setCpf(cpf);
        pessoa.setNascimento(LocalDate.of(1990, 1, 1));
        pessoa.setEmail("joao@email.com");
        pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE);
        
        com.luisgmr.senai.backend.domain.Endereco endereco = new com.luisgmr.senai.backend.domain.Endereco();
        endereco.setCep("88701000");
        endereco.setRua("Rua Teste");
        endereco.setNumero(123);
        endereco.setCidade("Tubarão");
        endereco.setEstado("SC");
        pessoa.setEndereco(endereco);
        
        pessoaRepository.save(pessoa);

        mvc.perform(post("/pessoa/cpf/{cpf}/integrar", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());
    }
}