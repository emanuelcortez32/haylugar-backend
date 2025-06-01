package ar.com.velascosoft.haylugar.entities.converters;

import ar.com.velascosoft.haylugar.pojo.Address;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@ReadingConverter
public class JsonToSpotAddressConverter implements Converter<Json, Address> {
    private final ObjectMapper objectMapper;
    @Override
    public Address convert(Json source) {
        try {
            return objectMapper.readValue(source.asString(), new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Problem while parsing JSON: {}", source, e);
        }

        return Address.builder().build();
    }
}
