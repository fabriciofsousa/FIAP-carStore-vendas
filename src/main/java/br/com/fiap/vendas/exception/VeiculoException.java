package br.com.fiap.vendas.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class VeiculoException extends RuntimeException{

    Logger logger = LoggerFactory.getLogger(VeiculoException.class);

    public VeiculoException(String message) {
        super(message);
    }

    public VeiculoException(HttpStatus httpStatus, String message) {
        super(message);
        logger.error("HTTP Status: {} - Message: {}", httpStatus, message);
    }
}
