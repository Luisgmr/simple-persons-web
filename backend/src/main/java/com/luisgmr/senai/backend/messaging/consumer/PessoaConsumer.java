package com.luisgmr.senai.backend.messaging.consumer;

import com.luisgmr.senai.backend.config.RabbitMQConfig;
import com.luisgmr.senai.backend.domain.Pessoa;
import com.luisgmr.senai.backend.dto.request.IntegrarPessoaRequestDTO;
import com.luisgmr.senai.backend.dto.response.PessoaConsultaResponseDTO;
import com.luisgmr.senai.backend.repository.PessoaRepository;
import com.luisgmr.senai.backend.domain.SituacaoIntegracao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PessoaConsumer {
    private final RestTemplate restTemplate = new RestTemplate();
    private final PessoaRepository repository;
    private final PessoaRepository pessoaRepository;

    @Value("${api.pessoa.url:http://localhost:8081}")
    private String apiUrl;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    @Transactional
    public void processar(IntegrarPessoaRequestDTO dto) {
        PessoaConsultaResponseDTO pessoaConsultaResponseDTO = dto.getPessoaConsultaResponseDTO();
        log.info("Consumindo pessoa {} da fila", pessoaConsultaResponseDTO.getCpf());
        Pessoa pessoa = pessoaRepository.findByCpf(pessoaConsultaResponseDTO.getCpf()).orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PessoaConsultaResponseDTO> request = new HttpEntity<>(pessoaConsultaResponseDTO, headers);

            if (dto.isBotaoIntegrar()) {
                if (dto.isCpfAlterado()) {
                    restTemplate.postForEntity(apiUrl + "/pessoa", request, Void.class);
                }
                restTemplate.exchange(apiUrl + "/pessoa/" + pessoaConsultaResponseDTO.getCpf(), HttpMethod.PUT, request, Void.class);
                log.info("Pessoa {} atualizada com sucesso via PUT", pessoaConsultaResponseDTO.getCpf());
            } else {
                restTemplate.postForEntity(apiUrl + "/pessoa", request, Void.class);
                log.info("Pessoa {} integrada com sucesso via POST", pessoaConsultaResponseDTO.getCpf());
            }

            pessoa.setSituacaoIntegracao(SituacaoIntegracao.SUCESSO);
            log.info("Integração da pessoa {} finalizada com sucesso", pessoaConsultaResponseDTO.getCpf());
            
        } catch (RestClientException ex) {
            log.error("Erro ao integrar pessoa {}: {}", pessoaConsultaResponseDTO.getCpf(), ex.getMessage());
            pessoa.setSituacaoIntegracao(SituacaoIntegracao.ERRO);
        } catch (Exception ex) {
            log.error("Erro inesperado ao processar pessoa {}: {}", pessoaConsultaResponseDTO.getCpf(), ex.getMessage());
            pessoa.setSituacaoIntegracao(SituacaoIntegracao.ERRO);
        }
        
        repository.save(pessoa);
    }
}