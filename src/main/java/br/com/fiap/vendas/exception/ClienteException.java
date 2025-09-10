package br.com.fiap.vendas.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ClienteException extends RuntimeException{

    Logger logger = LoggerFactory.getLogger(ClienteException.class);

    public ClienteException(String message) {
        super(message);
    }

    public ClienteException(HttpStatus httpStatus, String message) {
        super(message);
        logger.error("HTTP Status: {} - Message: {}", httpStatus, message);
    }
}
