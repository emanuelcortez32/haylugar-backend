package ar.com.greenbundle.haylugar.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateBookingResponse extends GenericResponse {
    private String id;

    @Builder
    public CreateBookingResponse(boolean success,
                              String message,
                              String id) {
        super(success, message);
        this.id = id;
    }
}
