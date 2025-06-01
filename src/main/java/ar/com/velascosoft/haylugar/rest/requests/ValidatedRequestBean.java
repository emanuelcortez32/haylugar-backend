package ar.com.velascosoft.haylugar.rest.requests;

import ar.com.velascosoft.haylugar.exceptions.BadRequestBodyException;
import ar.com.velascosoft.haylugar.pojo.ValidateResult;
import ar.com.velascosoft.haylugar.util.validation.ValidateUtils;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

public abstract class ValidatedRequestBean {
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
