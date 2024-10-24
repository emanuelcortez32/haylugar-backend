package ar.com.greenbundle.haylugar.rest.responses;

import ar.com.greenbundle.haylugar.pojo.constants.Gender;
import ar.com.greenbundle.haylugar.pojo.constants.Nationality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String name;
    private String surname;
    private String dni;
    private Gender gender;
    private String birthDate;
    private Nationality nationality;
}
