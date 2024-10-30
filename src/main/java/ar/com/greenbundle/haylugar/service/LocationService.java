package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.exceptions.FeatureException;
import ar.com.greenbundle.haylugar.pojo.Address;
import ar.com.greenbundle.haylugar.pojo.constants.AllowedArea;
import ar.com.greenbundle.haylugar.rest.clients.openstreetmaps.AddressData;
import ar.com.greenbundle.haylugar.rest.clients.openstreetmaps.OpenStreetMapClient;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Stream;

@Service
public class LocationService {
    @Value("#{new Boolean('${app.features.location.search.enabled:false}')}")
    private Boolean isLocationSearchEnabled;
    @Autowired
    private OpenStreetMapClient openStreetMapClient;

    public boolean checkIfPointInsideAllowedArea(double longitude, double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel());

        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        Stream<Polygon> polygons = Arrays.stream(AllowedArea.values()).map(allowedArea -> Arrays.stream(allowedArea.coordinates)
                .map(coordinate -> new Coordinate(coordinate[0], coordinate[1])).toList())
                .map(coordinate -> geometryFactory.createLinearRing(coordinate.toArray(new Coordinate[0])))
                .map(linearRing -> new Polygon(linearRing, null, geometryFactory));

        return polygons.noneMatch(polygon -> polygon.contains(point));
    }

    public Mono<Address> getAddressFromCoordinate(double longitude, double latitude) {

        if(!isLocationSearchEnabled)
            return Mono.error(new FeatureException("Feature location search is disabled"));

        return openStreetMapClient.getDirectionNameWithCoordinates(longitude, latitude)
                .flatMap(addressResponse -> {
                    if(!addressResponse.isSuccess())
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
