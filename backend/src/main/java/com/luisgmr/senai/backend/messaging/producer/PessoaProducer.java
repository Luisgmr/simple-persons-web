package com.luisgmr.senai.backend.messaging.producer;

import com.luisgmr.senai.backend.config.RabbitMQConfig;
import com.luisgmr.senai.backend.dto.response.PessoaConsultaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PessoaProducer {
    private final RabbitTemplate rabbitTemplate;

    public void enviarParaFila(PessoaConsultaResponseDTO dto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, dto);
    }
}
