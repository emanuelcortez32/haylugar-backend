package ar.com.velascosoft.haylugar.rest.clients.openstreetmaps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressData {
    @JsonProperty("display_name")
    private String displayName;
    private String licence;
    @JsonProperty("address")
    private AddressDetail addressDetail;
}
