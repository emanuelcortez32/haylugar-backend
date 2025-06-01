package ar.com.velascosoft.haylugar.rest.responses;

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
public class CreateVehicleResponse extends ApiResponse {
    @Builder
    public CreateVehicleResponse(boolean success,
                                 String message,
                                 String id) {
        super(success, message);
        this.data = Map.of("id", id);
    }
}
