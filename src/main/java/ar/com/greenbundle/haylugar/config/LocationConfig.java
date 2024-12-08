package ar.com.greenbundle.haylugar.config;

import ar.com.greenbundle.haylugar.providers.location.LocationProvider;
import ar.com.greenbundle.haylugar.providers.location.OpenStreetMapProvider;
import ar.com.greenbundle.haylugar.rest.clients.openstreetmaps.OpenStreetMapClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocationConfig {
    @Bean(name = "openStreetMapsProvider")
    public LocationProvider openStreetMapsProvider(ObjectMapper objectMapper) {
        OpenStreetMapClient client = new OpenStreetMapClient(objectMapper);

        return new OpenStreetMapProvider(client);
    }
}
