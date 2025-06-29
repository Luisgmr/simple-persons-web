package com.luisgmr.senai.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisgmr.senai.backend.domain.Pessoa;
import com.luisgmr.senai.backend.domain.SituacaoIntegracao;
import com.luisgmr.senai.backend.dto.request.CadastrarPessoaRequestDTO;
import com.luisgmr.senai.backend.repository.PessoaRepository;
import com.luisgmr.senai.backend.testutil.Generator;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class BackendApiIntegrationTest {

    private static final Network NET = Network.newNetwork();

    @Container
    static final PostgreSQLContainer<?> apiPostgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("pessoa_api")
                    .withUsername("dev")
                    .withPassword("dev")
                    .withNetwork(NET)
                    .withNetworkAliases("postgres-api");

    @Container
    static final PostgreSQLContainer<?> backendPostgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("pessoa_web")
                    .withUsername("dev")
                    .withPassword("dev")
                    .withNetwork(NET)
                    .withNetworkAliases("postgres-web");

    @Container
    static final RabbitMQContainer rabbit =
            new RabbitMQContainer("rabbitmq:3.13-alpine")
                    .withNetwork(NET)
                    .withNetworkAliases("rabbitmq");

    @Container
    static final GenericContainer<?> api =
            new GenericContainer<>("pessoa-api:local")
                    .withNetwork(NET)
                    .withNetworkAliases("api")
                    .withEnv("DB_HOST", "postgres-api")
                    .withEnv("DB_USER", "dev")
                    .withEnv("DB_PASS", "dev")
                    .withEnv("SPRING_RABBITMQ_HOST", "rabbitmq")
                    .withExposedPorts(8081)
                    .waitingFor(
                            Wait.forHttp("/actuator/health")
                                    .forPort(8081)
                                    .forStatusCode(200)
                    )
                    .withStartupTimeout(Duration.ofMinutes(2))
                    .dependsOn(apiPostgres, rabbit);

    @DynamicPropertySource
    static void testProps(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url", backendPostgres::getJdbcUrl);
        reg.add("spring.datasource.username", () -> "dev");
        reg.add("spring.datasource.password", () -> "dev");

        reg.add("spring.rabbitmq.host", rabbit::getHost);
        reg.add("spring.rabbitmq.port", rabbit::getAmqpPort);

        reg.add("api.pessoa.url", () -> "http://localhost:" + api.getMappedPort(8081));
    }

    @Autowired MockMvc mvc;
    @Autowired PessoaRepository pessoaRepository;
    @Autowired ObjectMapper mapper;
    @Autowired CachingConnectionFactory cf;
    @Autowired EntityManager em;

    @Test
    @DisplayName("Fluxo completo: Backend → RabbitMQ → API")
    void fluxoCompletoBackendApi() throws Exception {
        String cpf = Generator.randomCpf();

        CadastrarPessoaRequestDTO dto = new CadastrarPessoaRequestDTO();
        dto.setNome("João Silva");
        dto.setDataNascimento(LocalDate.of(1990, 1, 1));
        dto.setCpf(cpf);
        dto.setEmail("joao@email.com");
        var end = new CadastrarPessoaRequestDTO.EnderecoDTO();
        end.setCep("88701000");
        end.setRua("Rua Teste");
        end.setNumero(123);
        end.setCidade("Tubarão");
        end.setEstado("SC");
        dto.setEndereco(end);

        mvc.perform(post("/pessoa")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        Pessoa pessoa = pessoaRepository.findByCpf(cpf).orElseThrow();
        assertThat(pessoa.getSituacaoIntegracao()).isEqualTo(SituacaoIntegracao.PENDENTE);

        await()
                .atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    em.clear();
                    Pessoa p = pessoaRepository.findByCpf(cpf).orElseThrow();
                    assertThat(p.getSituacaoIntegracao())
                            .isEqualTo(SituacaoIntegracao.SUCESSO);
                });

        mvc.perform(get("/pessoa/cpf/{cpf}/integrada", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.situacaoIntegracao").value("Sucesso"));
    }

    @AfterAll
    static void tearDown(@Autowired CachingConnectionFactory cf) {
        if (cf != null) cf.destroy();
    }
}
