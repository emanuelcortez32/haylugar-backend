package ar.com.velascosoft.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StartBookingResponse extends BookingActionResponse {
    @Builder
    public StartBookingResponse(boolean success,
                                String message,
                                String startTime) {
        super(success, message);
        this.data = Map.of("startTime", startTime);
    }
}
