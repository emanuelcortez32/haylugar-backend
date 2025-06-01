package ar.com.velascosoft.haylugar.pojo;

import ar.com.velascosoft.haylugar.dto.UserPaymentCardDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String identificationType;
    private String identificationNumber;
    private List<UserPaymentCardDto> cards;
}
