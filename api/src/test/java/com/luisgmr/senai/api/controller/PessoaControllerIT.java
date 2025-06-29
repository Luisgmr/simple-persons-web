package com.luisgmr.senai.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisgmr.senai.api.TestcontainersConfiguration;
import com.luisgmr.senai.api.domain.Pessoa;
import com.luisgmr.senai.api.dto.PessoaRequestDTO;
import com.luisgmr.senai.api.repository.PessoaRepository;
import com.luisgmr.senai.api.testutil.Generator;
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
        PessoaRequestDTO dto = new PessoaRequestDTO();
        dto.setNome("João Silva");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setCpf(Generator.randomCpf());
        dto.setEmail("joao@email.com");
        
        PessoaRequestDTO.EnderecoDTO endereco = new PessoaRequestDTO.EnderecoDTO();
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
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Erro ao criar pessoa com CPF duplicado")
    void criarPessoa_cpfDuplicado() throws Exception {
        String cpf = Generator.randomCpf();
        
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Maria");
        pessoa.setCpf(cpf);
        pessoaRepository.save(pessoa);

        PessoaRequestDTO dto = new PessoaRequestDTO();
        dto.setNome("João Silva");
        dto.setCpf(cpf);

        mvc.perform(post("/pessoa")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Buscar pessoa por CPF")
    void buscarPorCpf() throws Exception {
        String cpf = Generator.randomCpf();
        
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setCpf(cpf);
        pessoa.setNascimento(LocalDate.of(1990, 1, 1));
        pessoaRepository.save(pessoa);

        mvc.perform(get("/pessoa/cpf/{cpf}", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value(cpf));
    }

    @Test
    @DisplayName("Atualizar pessoa")
    void atualizarPessoa() throws Exception {
        String cpf = Generator.randomCpf();
        
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setCpf(cpf);
        pessoa.setNascimento(LocalDate.of(1990, 1, 1));
        pessoaRepository.save(pessoa);

        PessoaRequestDTO dto = new PessoaRequestDTO();
        dto.setNome("João Silva Atualizado");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setCpf(cpf);
        dto.setEmail("joao.novo@email.com");

        mvc.perform(put("/pessoa/{cpf}", cpf)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"));
    }

    @Test
    @DisplayName("Deletar pessoa")
    void deletarPessoa() throws Exception {
        String cpf = Generator.randomCpf();
        
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João Silva");
        pessoa.setCpf(cpf);
        pessoaRepository.save(pessoa);

        mvc.perform(delete("/pessoa/cpf/{cpf}", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").exists());
    }
}