package ar.com.velascosoft.haylugar.rest.responses;

import ar.com.velascosoft.haylugar.pojo.Payment;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AddMinutesResponse extends ApiResponse {
    @Builder
    public AddMinutesResponse(boolean success,
                              String message,
                              Payment payment) {
        super(success, message);
        this.data = Map.of("initUrl", payment.getInitUrl(), "referenceId", payment.getId());
    }
}
