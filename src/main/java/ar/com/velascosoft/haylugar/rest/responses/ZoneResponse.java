package ar.com.velascosoft.haylugar.rest.responses;

import ar.com.velascosoft.haylugar.pojo.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneResponse {
    private String id;
    private String name;
    private String description;
    private List<Location> coordinates;
    @Builder.Default
    private String type = "Polygon";
}
