package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CancelBookingResponse extends BookingActionResponse {
    private String reason;

    @Builder
    public CancelBookingResponse(boolean success,
                                 String message,
                                 String reason) {
        super(success, message);
        this.reason = reason;
    }
}
