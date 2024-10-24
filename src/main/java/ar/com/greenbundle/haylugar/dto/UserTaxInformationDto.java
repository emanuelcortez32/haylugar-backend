package ar.com.greenbundle.haylugar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTaxInformationDto {
    private String cuit;
    private String address;
}
