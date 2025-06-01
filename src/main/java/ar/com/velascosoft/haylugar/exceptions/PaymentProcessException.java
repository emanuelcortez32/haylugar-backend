package ar.com.velascosoft.haylugar.exceptions;

public class PaymentProcessException extends RuntimeException {
    public PaymentProcessException(String msg) {
        super(msg);
    }
}
