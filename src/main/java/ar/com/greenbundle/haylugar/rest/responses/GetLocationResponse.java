package ar.com.greenbundle.haylugar.rest.responses;

import ar.com.greenbundle.haylugar.pojo.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetLocationResponse extends GenericResponse {
    @Builder
    public GetLocationResponse(boolean success,
                               String message,
                               Address address) {
        super(success, message);
        this.data = Map.of("address", address);
    }
}
