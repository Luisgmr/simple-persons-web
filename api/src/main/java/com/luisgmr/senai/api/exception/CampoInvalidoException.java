package com.luisgmr.senai.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CampoInvalidoException extends RuntimeException {
    public CampoInvalidoException(String message) {
        super(message);
    }
}