package com.luisgmr.senai.backend.validation;

import com.luisgmr.senai.backend.exception.CampoInvalidoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PessoaValidacao {
    public void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new CampoInvalidoException("Nome é obrigatório");
        }
        
        String[] nomes = nome.trim().split("\\s+");
        if (nomes.length < 2) {
            throw new CampoInvalidoException("Nome deve ter mais de 1 nome");
        }
        
        for (String n : nomes) {
            if (!n.matches("[A-ZÀ-ſ][a-zÀ-ſ]+")) {
                throw new CampoInvalidoException("A primeira letra de cada nome deve ser maiúscula, e as demais minúsculas");
            }
        }
    }
    
    public void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
            throw new CampoInvalidoException("Data de nascimento não pode ser maior que a data atual");
        }
    }
    
    public void validarEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(emailRegex)) {
                throw new CampoInvalidoException("Email inválido");
            }
        }
    }
    
    public void validarCpf(String cpf) {
        if (cpf != null && !cpf.trim().isEmpty()) {
            String cpfLimpo = cpf.replaceAll("[^0-9]", "");
            if (cpfLimpo.length() != 11 || !isValidCPF(cpfLimpo)) {
                throw new CampoInvalidoException("CPF inválido");
            }
        }
    }
    
    public void validarEnderecoCompleto(String cep, String rua, Integer numero, String cidade, String estado) {
        if (cep != null && !cep.trim().isEmpty()) {
            if (rua == null || rua.trim().isEmpty() ||
                numero == null ||
                cidade == null || cidade.trim().isEmpty() ||
                estado == null || estado.trim().isEmpty()) {
                throw new CampoInvalidoException("Se informado CEP, todos os campos do endereço devem ser preenchidos");
            }
        }
    }
    
    public boolean isCamposPreenchidos(String nome, LocalDate dataNascimento, String cpf, String email,
                                       String cep, String rua, Integer numero, String cidade, String estado) {
        return nome != null && !nome.trim().isEmpty() &&
               dataNascimento != null &&
               cpf != null && !cpf.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               cep != null && !cep.trim().isEmpty() &&
               rua != null && !rua.trim().isEmpty() &&
               numero != null &&
               cidade != null && !cidade.trim().isEmpty() &&
               estado != null && !estado.trim().isEmpty();
    }

    private boolean isValidCPF(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int[] digits = cpf.chars().map(c -> c - '0').toArray();

        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += digits[i] * (10 - i);
        }
        int digit1 = 11 - (sum1 % 11);
        if (digit1 >= 10) digit1 = 0;

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += digits[i] * (11 - i);
        }
        int digit2 = 11 - (sum2 % 11);
        if (digit2 >= 10) digit2 = 0;

        return digits[9] == digit1 && digits[10] == digit2;
    }

}