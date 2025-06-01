package ar.com.velascosoft.haylugar.rest.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ProcessPaymentRequest extends ValidatedRequestBean {
    @NotNull(message = "OrderId is required")
    private String orderId;
}
