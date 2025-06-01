package ar.com.velascosoft.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserBookingsResponse extends ApiResponse {
    @Builder
    public GetUserBookingsResponse(boolean success,
                                   String message,
                                   List<BookingResponse> bookings) {
        super(success, message);
        this.data = Map.of("bookings", bookings);
    }
}
