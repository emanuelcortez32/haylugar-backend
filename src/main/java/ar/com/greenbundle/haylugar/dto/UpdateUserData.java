package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.dto.constants.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserData {
    private UserState userState;
    private UserProfile profile;
}
