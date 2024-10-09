package ar.com.greenbundle.haylugar.rest.clients.openstreetmaps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDetail {
    @JsonProperty("house_number")
    private String houseNumber;
    private String road;
    private String quarter;
    private String suburb;
    @JsonProperty("city_district")
    private String cityDistrict;
    private String city;
    @JsonProperty("state_district")
    private String stateDistrict;
    private String state;
    private String postcode;
    private String country;
}
