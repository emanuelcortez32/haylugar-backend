package ar.com.greenbundle.haylugar.exceptions;

public class BadRequestBodyException extends RuntimeException {
    public BadRequestBodyException(String msg) {
        super(msg);
    }
}
