package ar.com.velascosoft.haylugar.entities.converters;

import ar.com.velascosoft.haylugar.pojo.PaymentTransactionDetail;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.io.IOException;
import java.util.List;

@Slf4j
@AllArgsConstructor
@ReadingConverter
public class JsonToPaymentTransactionDetailsConverter implements Converter<Json, List<PaymentTransactionDetail>> {
    private final ObjectMapper objectMapper;
    @Override
    public List<PaymentTransactionDetail> convert(Json source) {
        try {
            return objectMapper.readValue(source.asString(), new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Problem while parsing JSON: {}", source, e);
        }

        return List.of();
    }
}
