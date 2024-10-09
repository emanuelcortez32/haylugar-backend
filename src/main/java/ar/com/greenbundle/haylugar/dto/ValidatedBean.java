package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.exceptions.BadRequestBodyException;
import ar.com.greenbundle.haylugar.util.ValidateUtils;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

public abstract class ValidatedBean {
    public void validate() {

        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Validator validator = validatorFactory.getValidator();

        ValidateResult validateResult = ValidateUtils.validateBean(this, validator);

        if(!validateResult.isValid())
            throw new BadRequestBodyException(validateResult.getMessage());
    }
}
