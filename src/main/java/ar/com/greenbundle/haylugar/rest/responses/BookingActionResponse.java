package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookingActionResponse extends ApiResponse {
    public BookingActionResponse(boolean success,
                                 String message) {
        super(success, message);
    }
}
