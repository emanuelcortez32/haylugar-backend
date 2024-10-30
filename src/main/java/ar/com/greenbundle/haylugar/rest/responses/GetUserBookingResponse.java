package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserBookingResponse extends ApiResponse {
    @Builder
    public GetUserBookingResponse(boolean success,
                                  String message,
                                  BookingResponse bookingResponse) {
        super(success, message);
        this.data = Map.of("booking", bookingResponse);
    }
}
