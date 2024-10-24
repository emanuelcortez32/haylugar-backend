package ar.com.greenbundle.haylugar.rest.requests;

import ar.com.greenbundle.haylugar.dto.UserProfileDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import static jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CreateUserRequest extends ValidatedRequestBean {
    @NotNull(message = "Email is required")
    @Email(message = "Email address is invalid", flags = {CASE_INSENSITIVE})
    private String email;
    @NotNull(message = "Password is required")
    @Length(min = 8, max = 20)
    @Pattern(regexp = "^(.*[0-9].*[A-Z].*)|(.*[A-Z].*[0-9].*)$", message =  "Password requirements Have eight characters or more\n" +
                                                                            "Include a capital letter\n" +
                                                                            "Use at least one lowercase letter\n" +
                                                                            "Consists of at least one digit\n" +
                                                                            "Need to have one special symbol (i.e., @, #, $, %, etc.)\n" +
                                                                            "Doesn’t contain space, tab, etc.")
    private String password;
    @NotNull(message = "User profile is required")
    private UserProfileDto profile;
}
