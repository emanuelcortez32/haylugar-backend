package ar.com.velascosoft.haylugar.service;

import ar.com.velascosoft.haylugar.entities.app.ZoneEntity;
import ar.com.velascosoft.haylugar.exceptions.FeatureException;
import ar.com.velascosoft.haylugar.pojo.Address;
import ar.com.velascosoft.haylugar.providers.location.LocationProvider;
import ar.com.velascosoft.haylugar.service.app.ZoneService;
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

import java.util.List;

@Service
public class LocationService {
    @Value("#{new Boolean('${app.features.location.search.enabled:false}')}")
    private Boolean isLocationSearchEnabled;
    @Autowired
    private LocationProvider locationProvider;
    @Autowired
    private ZoneService zoneService;

    public Mono<ZoneEntity> getAssignedZoneFromCoordinates(double longitude, double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel());

        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        return zoneService.getAllZones()
                .collectList()
                .flatMap(zones -> {

                    for (ZoneEntity zone : zones) {
                        List<Coordinate> coordinates = zone.getCoordinates().stream()
                                .map(coordinate -> new Coordinate(coordinate.getX(), coordinate.getY()))
                                .toList();

                        LinearRing linearRing = geometryFactory.createLinearRing(coordinates.toArray(new Coordinate[0]));
                        Polygon polygon = new Polygon(linearRing, null, geometryFactory);

                        if(polygon.contains(point))
                            return Mono.just(zone);
                    }

                    return Mono.empty();
                });
    }

    public Mono<Address> getAddressFromCoordinate(double longitude, double latitude) {

        if(!isLocationSearchEnabled)
            return Mono.error(new FeatureException("Feature location search is disabled"));

        return locationProvider.getAddressFromCoordinate(longitude, latitude);
    }
}
