package ar.com.velascosoft.haylugar.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Builder.Default
    private double latitude = 0;
    @Builder.Default
    private double longitude = 0;
    @Builder.Default
    private String type = "Point";
}
