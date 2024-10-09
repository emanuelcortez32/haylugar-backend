package ar.com.greenbundle.haylugar.exceptions;

public class SpotCapacityExceeded extends RuntimeException {
    public SpotCapacityExceeded(String msg) {
        super(msg);
    }
}
