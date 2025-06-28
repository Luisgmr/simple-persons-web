package com.luisgmr.senai.api.validation;

import com.luisgmr.senai.api.exception.CampoInvalidoException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;

@Service
public class PessoaValidacao {
    private final RestTemplate restTemplate = new RestTemplate();
    
    public void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
            throw new CampoInvalidoException("Data de nascimento não pode ser maior que a data atual");
        }
    }
    
    public void validarCep(String cep) {
        if (cep == null || cep.trim().isEmpty()) {
            throw new CampoInvalidoException("CEP é obrigatório");
        }
        
        try {
            String url = "https://viacep.com.br/ws/" + cep + "/json/";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || response.containsKey("erro")) {
                throw new CampoInvalidoException("CEP inválido ou não encontrado");
            }
        } catch (Exception e) {
            throw new CampoInvalidoException("Erro ao validar CEP");
        }
    }

}