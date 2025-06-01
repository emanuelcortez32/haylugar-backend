package ar.com.velascosoft.haylugar.providers.location;

import ar.com.velascosoft.haylugar.pojo.Address;
import ar.com.velascosoft.haylugar.rest.clients.openstreetmaps.AddressData;
import ar.com.velascosoft.haylugar.rest.clients.openstreetmaps.OpenStreetMapClient;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class OpenStreetMapProvider implements LocationProvider {
    private final OpenStreetMapClient openStreetMapClient;

    @Override
    public Mono<Address> getAddressFromCoordinate(double longitude, double latitude) {
        return openStreetMapClient.getDirectionNameWithCoordinates(longitude, latitude)
                .flatMap(addressResponse -> {
                    if (!addressResponse.isSuccess())
                        return Mono.just(Address.builder().displayName("Unable to locate").build());

                    AddressData addressData = addressResponse.getData();

                    return Mono.just(Address.builder()
                            .displayName(addressData.getDisplayName())
                            .country(addressData.getAddressDetail().getCountry())
                            .state(addressData.getAddressDetail().getState())
                            .stateDistrict(addressData.getAddressDetail().getState())
                            .city(addressData.getAddressDetail().getCity())
                            .cityDistrict(addressData.getAddressDetail().getCityDistrict())
                            .suburb(addressData.getAddressDetail().getSuburb())
                            .quarter(addressData.getAddressDetail().getQuarter())
                            .road(addressData.getAddressDetail().getRoad())
                            .houseNumber(addressData.getAddressDetail().getHouseNumber())
                            .postcode(addressData.getAddressDetail().getPostcode())
                            .license(addressData.getLicence())
                            .build());
                });
    }
}
