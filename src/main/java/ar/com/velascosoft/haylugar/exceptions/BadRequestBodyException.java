package ar.com.velascosoft.haylugar.exceptions;

public class BadRequestBodyException extends RuntimeException {
    public BadRequestBodyException(String msg) {
        super(msg);
    }
}
