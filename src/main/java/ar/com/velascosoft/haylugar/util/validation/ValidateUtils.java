package ar.com.velascosoft.haylugar.util.validation;

import ar.com.velascosoft.haylugar.pojo.ValidateResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ValidateUtils {
    public static ValidateResult validateBean(Object bean, Validator validator) {
        ObjectMapper objectMapper = new ObjectMapper();

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(bean);

        if(!constraintViolations.isEmpty()) {
            Map<String, String> mapOfErrors = new HashMap<>();

            constraintViolations.forEach(violation ->
                    mapOfErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));

            try {
                String mapOfErrorsString = objectMapper.writeValueAsString(mapOfErrors);

                return ValidateResult.builder()
                        .isValid(false)
                        .constraintViolations(constraintViolations)
                        .message(mapOfErrorsString)
                        .build();

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return ValidateResult.builder().build();
    }
}
