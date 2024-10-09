package ar.com.greenbundle.haylugar.rest.responses;

import ar.com.greenbundle.haylugar.dto.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetLocationResponse extends GenericResponse {
    @JsonProperty("data")
    private Address address;

    @Builder
    public GetLocationResponse(boolean success,
                               String message,
                               Address address) {
        super(success, message);
        this.address = address;
    }
}
