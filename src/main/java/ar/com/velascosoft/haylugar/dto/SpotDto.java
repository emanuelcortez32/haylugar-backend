package ar.com.velascosoft.haylugar.dto;

import ar.com.velascosoft.haylugar.entities.SpotEntity;
import ar.com.velascosoft.haylugar.pojo.Address;
import ar.com.velascosoft.haylugar.pojo.constants.SpotState;
import ar.com.velascosoft.haylugar.pojo.constants.SpotType;
import io.r2dbc.postgresql.codec.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SpotDto extends EntityDto<SpotEntity, SpotDto> {
    private UserDto landLord;
    private SpotType type;
    private Point location;
    private Address address;
    private String zone;
    private int capacity;
    private int capacityAvailable;
    private double pricePerMinute;
    private String description;
    private SpotState state;
    private String[] photos;

    @Builder
    public SpotDto(String id, LocalDateTime createdAt, Long version, UserDto landLord, SpotType type, Point location, Address address, String zone, int capacity, int capacityAvailable, double pricePerMinute, String description, SpotState state, String[] photos) {
        super(id, createdAt, version);
        this.landLord = landLord;
        this.type = type;
        this.location = location;
        this.address = address;
        this.zone = zone;
        this.capacity = capacity;
        this.capacityAvailable = capacityAvailable;
        this.pricePerMinute = pricePerMinute;
        this.description = description;
        this.state = state;
        this.photos = photos;
    }

    @Override
    public SpotDto dtoFromEntity(SpotEntity entity) {
        return SpotDto.builder()
                .id(entity.getId())
                .landLord(UserDto.builder().id(entity.getLandLordUserId()).build())
                .type(entity.getType())
                .state(entity.getState())
                .photos(entity.getPhotos())
                .capacity(entity.getCapacity())
                .capacityAvailable(entity.getCapacityAvailable())
                .location(entity.getLocation())
                .address(entity.getAddress())
                .zone(entity.getZone())
                .pricePerMinute(entity.getPricePerMinute())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }

    @Override
    public SpotEntity dtoToEntity(SpotDto dto) {
        return SpotEntity.builder()
                .id(dto.getId())
                .landLordUserId(dto.getLandLord().getId())
                .type(dto.getType())
                .capacity(dto.getCapacity())
                .capacityAvailable(dto.getCapacityAvailable())
                .location(dto.getLocation())
                .address(dto.getAddress())
                .zone(dto.getZone())
                .pricePerMinute(dto.getPricePerMinute())
                .description(dto.getDescription())
                .photos(dto.getPhotos())
                .state(dto.getState())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
