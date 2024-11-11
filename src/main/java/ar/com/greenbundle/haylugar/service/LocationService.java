package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dao.SpotDao;
import ar.com.greenbundle.haylugar.exceptions.FeatureException;
import ar.com.greenbundle.haylugar.pojo.Address;
import ar.com.greenbundle.haylugar.pojo.constants.AllowedZone;
import ar.com.greenbundle.haylugar.providers.location.LocationProvider;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    @Value("#{new Boolean('${app.features.location.search.enabled:false}')}")
    private Boolean isLocationSearchEnabled;
    @Autowired
    private LocationProvider locationProvider;
    @Autowired
    private SpotDao spotDao;

    public Optional<AllowedZone> getAssignedZoneFromCoordinates(double longitude, double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel());

        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        for (AllowedZone zone : AllowedZone.values()) {
            List<Coordinate> coordinates = Arrays.stream(zone.coordinates)
                    .map(coordinate -> new Coordinate(coordinate[0], coordinate[1]))
                    .toList();

            LinearRing linearRing = geometryFactory.createLinearRing(coordinates.toArray(new Coordinate[0]));
            Polygon polygon = new Polygon(linearRing, null, geometryFactory);

            if(polygon.contains(point))
                return Optional.of(zone);
        }

        return Optional.empty();
    }

    public Mono<Address> getAddressFromCoordinate(double longitude, double latitude) {

        if(!isLocationSearchEnabled)
            return Mono.error(new FeatureException("Feature location search is disabled"));

        return locationProvider.getAddressFromCoordinate(longitude, latitude);
    }
}
