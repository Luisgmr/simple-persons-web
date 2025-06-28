package com.luisgmr.senai.backend.config;

import com.luisgmr.senai.backend.dto.response.ErroResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> retornoExcecaoValidacaoCampos(MethodArgumentNotValidException exception, HttpServletRequest request) {
        FieldError firstError = exception.getBindingResult().getFieldErrors().getFirst();

        ErroResponseDTO error = ErroResponseDTO.builder()
                .timestamp(ZonedDateTime.now().toString())
                .erro(exception.getClass().getSimpleName())
                .mensagem(capitalize(firstError.getField() + " " + firstError.getDefaultMessage()))
                .caminho("backend/" + request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> retornoExcecaoPadrao(Exception exception, HttpServletRequest request) {
        ResponseStatus statusAnnotation = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
        HttpStatus status = (statusAnnotation != null ? statusAnnotation.value() : HttpStatus.BAD_REQUEST);

        ErroResponseDTO error = ErroResponseDTO.builder()
                .timestamp(ZonedDateTime.now().toString())
                .erro(exception.getClass().getSimpleName())
                .mensagem(exception.getMessage())
                .caminho(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    private String capitalize(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}


