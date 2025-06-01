package ar.com.velascosoft.haylugar.providers.location;

import ar.com.velascosoft.haylugar.pojo.Address;
import reactor.core.publisher.Mono;

public interface LocationProvider {
    Mono<Address> getAddressFromCoordinate(double longitude, double latitude);
}
