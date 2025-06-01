package ar.com.velascosoft.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProcessPaymentResponse extends ApiResponse {
    @Builder
    public ProcessPaymentResponse(boolean success,
                                  String message) {
        super(success, message);
        this.data = Map.of();
    }
}
