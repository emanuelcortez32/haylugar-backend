package ar.com.greenbundle.haylugar.rest.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class UserLoginRequest extends ValidatedRequestBean {
    @NotNull(message = "Email is required")
    @Email(message = "Email address is invalid", flags = {CASE_INSENSITIVE})
    private String email;
    @NotNull(message = "Password is required")
    private String password;
}
