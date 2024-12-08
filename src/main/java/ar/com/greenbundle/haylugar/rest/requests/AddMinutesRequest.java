package ar.com.greenbundle.haylugar.rest.requests;

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
public class AddMinutesRequest extends ValidatedRequestBean {
    @NotNull(message = "Minutes is required")
    private int minutes;
}
