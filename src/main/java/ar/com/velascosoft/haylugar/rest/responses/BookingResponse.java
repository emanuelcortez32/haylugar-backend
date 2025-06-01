package ar.com.velascosoft.haylugar.rest.responses;

import ar.com.velascosoft.haylugar.pojo.constants.BookingState;
import ar.com.velascosoft.haylugar.pojo.constants.BookingUserAs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private String id;
    private String spotId;
    private String paymentId;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private long totalMinutes;
    private BookingState state;
    private BookingUserAs userAs;
}
