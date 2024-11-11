package ar.com.greenbundle.haylugar.rest.clients.openstreetmaps;

import ar.com.greenbundle.haylugar.rest.clients.ThirdPartyClientResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Slf4j
public class OpenStreetMapClient {
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public OpenStreetMapClient(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

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

    public Mono<ThirdPartyClientResponse<AddressData>> getDirectionNameWithCoordinates(double longitude, double latitude) {

        WebClient.RequestBodySpec bodySpec = (WebClient.RequestBodySpec) webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("format", "json")
                        .queryParam("lon", longitude)
                        .queryParam("lat", latitude)
                        .build());

        return exchange(bodySpec);
    }

    private Mono<ThirdPartyClientResponse<AddressData>> exchange(WebClient.RequestBodySpec bodySpec) {
        ThirdPartyClientResponse<AddressData> thirdPartyClientResponse = new ThirdPartyClientResponse<>();

        return bodySpec.exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        thirdPartyClientResponse.setSuccess(false);
                        thirdPartyClientResponse.setStatusCode(clientResponse.statusCode());

                        log.error("m=exchange : Send request error !!! [{}]",
                                clientResponse.statusCode());
                    } else {
                        thirdPartyClientResponse.setSuccess(true);
                        thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
                    }
                    return clientResponse.bodyToMono(String.class);
                })
                .map(rawResponse -> {
                    log.debug("m=exchange : Raw response [{}]", rawResponse);
                    if (thirdPartyClientResponse.isSuccess()) {
                        try {
                            thirdPartyClientResponse.setData(objectMapper.readValue(rawResponse, AddressData.class));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return thirdPartyClientResponse;
                });
    }
}
