package ar.com.velascosoft.haylugar.rest.requests;

import ar.com.velascosoft.haylugar.pojo.constants.Currency;
import ar.com.velascosoft.haylugar.pojo.constants.PaymentMethod;
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
public class CreateBookingRequest extends ValidatedRequestBean {
    @NotNull(message = "Spot ID is required")
    private String spotId;
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    @NotNull(message = "Currency is required")
    private Currency currency;
}
