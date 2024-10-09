package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StartBookingResponse extends BookingActionResponse {
    private String startedTime;

    @Builder
    public StartBookingResponse(boolean success,
                                String message,
                                String startedTime) {
        super(success, message);
        this.startedTime = startedTime;
    }
}
