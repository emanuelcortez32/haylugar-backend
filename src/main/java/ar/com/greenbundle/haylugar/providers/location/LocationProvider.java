package ar.com.greenbundle.haylugar.providers.location;

import ar.com.greenbundle.haylugar.pojo.Address;
import reactor.core.publisher.Mono;

public interface LocationProvider {
    Mono<Address> getAddressFromCoordinate(double longitude, double latitude);
}
