package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.dto.constants.SpotState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSpotData {
    private SpotState spotState;
    private String description;
    private String[] photos;
}
