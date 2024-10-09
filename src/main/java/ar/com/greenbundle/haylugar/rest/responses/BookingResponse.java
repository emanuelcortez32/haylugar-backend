package ar.com.greenbundle.haylugar.rest.responses;

import ar.com.greenbundle.haylugar.dto.constants.BookingState;
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
    private String paymentId;
    private String startTime;
    private String endTime;
    private long totalMinutes;
    private BookingState state;
}
