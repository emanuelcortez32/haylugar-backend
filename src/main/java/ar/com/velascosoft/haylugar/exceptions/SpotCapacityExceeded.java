package ar.com.velascosoft.haylugar.exceptions;

public class SpotCapacityExceeded extends RuntimeException {
    public SpotCapacityExceeded(String msg) {
        super(msg);
    }
}
