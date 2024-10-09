package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.dto.constants.Gender;
import ar.com.greenbundle.haylugar.dto.constants.Nationality;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class UserProfile extends ValidatedBean {
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Surname is required")
    private String surname;
    @NotNull(message = "DNI is required")
    private String dni;
    @NotNull(message = "Gender is required")
    private Gender gender;
    @NotNull(message = "Birth date is required")
    @Pattern(regexp = "^([0-2][0-9]||3[0-1])/(0[0-9]||1[0-2])/([0-9][0-9])?[0-9][0-9]$", message = "Birth date format must be dd/MM/yyyy")
    private String birthDate;
    @NotNull(message = "Nationality is required")
    private Nationality nationality;
    private UserTaxInformation taxInformation;
    private PaymentProfile paymentProfile;
}
