package ar.com.greenbundle.haylugar.rest.clients.openstreetmaps;

import ar.com.greenbundle.haylugar.rest.clients.ThirdPartyClientResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Component
public class OpenStreetMapClient {
    private final WebClient webClient;
    public OpenStreetMapClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

        this.webClient = WebClient
                .builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Mono<ThirdPartyClientResponse> getDirectionNameWithCoordinates(double longitude, double latitude) {

        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("format", "json")
                        .queryParam("lon", longitude)
                        .queryParam("lat", latitude)
                        .build())
                .retrieve()
                .bodyToMono(AddressData.class)
                .flatMap(addressData -> {
                    thirdPartyClientResponse.setSuccess(true);
                    thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
                    thirdPartyClientResponse.setData(addressData);

                    return Mono.just(thirdPartyClientResponse);
                })
                .onErrorResume(error -> {
                    thirdPartyClientResponse.setSuccess(false);
                    thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
                    thirdPartyClientResponse.setData(error.getMessage());

                    return Mono.just(thirdPartyClientResponse);
                });

    }
}
