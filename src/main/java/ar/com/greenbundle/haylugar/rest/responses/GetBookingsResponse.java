package ar.com.greenbundle.haylugar.rest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetBookingsResponse extends GenericResponse {
    @JsonProperty("data")
    private List<BookingResponse> bookings;

    @Builder
    public GetBookingsResponse(boolean success,
                               String message,
                               List<BookingResponse> bookings) {
        super(success, message);
        this.bookings = bookings;
    }
}
