package ar.com.velascosoft.haylugar.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String houseNumber;
    private String displayName;
    private String road;
    private String quarter;
    private String suburb;
    private String cityDistrict;
    private String city;
    private String stateDistrict;
    private String state;
    private String postcode;
    private String country;
    private String license;
}
