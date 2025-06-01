package ar.com.velascosoft.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CancelBookingResponse extends BookingActionResponse {

    @Builder
    public CancelBookingResponse(boolean success,
                                 String message,
                                 String reason) {
        super(success, message);
        this.data = Map.of("reason", reason);
    }
}
