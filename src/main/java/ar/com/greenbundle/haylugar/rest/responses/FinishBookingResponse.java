package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FinishBookingResponse extends BookingActionResponse {
    @Builder
    public FinishBookingResponse(boolean success,
                                 String message,
                                 long totalMinutes,
                                 String endTime,
                                 String paymentId) {
        super(success, message);
        this.data = Map.of(
                "paymentId", paymentId,
                "totalMinutes", totalMinutes,
                "endTime", endTime
        );
    }
}
