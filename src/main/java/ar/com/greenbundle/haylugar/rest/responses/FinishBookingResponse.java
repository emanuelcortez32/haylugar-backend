package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FinishBookingResponse extends BookingActionResponse {
    private long totalMinutes;
    private String paymentId;
    @Builder
    public FinishBookingResponse(boolean success,
                                 String message,
                                 long totalMinutes,
                                 String paymentId) {
        super(success, message);
        this.totalMinutes = totalMinutes;
        this.paymentId = paymentId;
    }
}
