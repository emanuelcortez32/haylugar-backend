package ar.com.greenbundle.haylugar.pojo;

import jakarta.validation.ConstraintViolation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateResult {
    @Builder.Default
    private boolean isValid = true;
    @Builder.Default
    private Set<ConstraintViolation<Object>> constraintViolations = Set.of();
    @Builder.Default
    private String message = "OK";
}
