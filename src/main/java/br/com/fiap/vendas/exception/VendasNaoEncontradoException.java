package br.com.fiap.vendas.exception;

public class VendasNaoEncontradoException extends RuntimeException{
    public VendasNaoEncontradoException(String message) {
        super(message);
    }
}
