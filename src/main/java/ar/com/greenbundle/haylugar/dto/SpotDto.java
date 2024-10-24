package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.entities.SpotEntity;
import ar.com.greenbundle.haylugar.pojo.Address;
import ar.com.greenbundle.haylugar.pojo.constants.SpotState;
import ar.com.greenbundle.haylugar.pojo.constants.SpotType;
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
public class SpotDto extends EntityDto {
    private UserDto landLord;
    private SpotType type;
    private Point location;
    private Address address;
    private int capacity;
    private double pricePerMinute;
    private String description;
    private SpotState state;
    private String[] photos;

    @Builder
    public SpotDto(String id, LocalDateTime createdAt, Long version, UserDto landLord, SpotType type, Point location, Address address, int capacity, double pricePerMinute, String description, SpotState state, String[] photos) {
        super(id, createdAt, version);
        this.landLord = landLord;
        this.type = type;
        this.location = location;
        this.address = address;
        this.capacity = capacity;
        this.pricePerMinute = pricePerMinute;
        this.description = description;
        this.state = state;
        this.photos = photos;
    }

    public static SpotDto.SpotDtoBuilder builderFromEntity(SpotEntity entity) {
        return SpotDto.builder()
                .id(entity.getId())
                .landLord(UserDto.builder().id(entity.getLandLordUserId()).build())
                .type(entity.getType())
                .state(entity.getState())
                .photos(entity.getPhotos())
                .capacity(entity.getCapacity())
                .location(entity.getLocation())
                .address(entity.getAddress())
                .pricePerMinute(entity.getPricePerMinute())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion());
    }

    public static SpotEntity mapToEntity(SpotDto dto) {
        return SpotEntity.builder()
                .id(dto.getId())
                .landLordUserId(dto.getLandLord().getId())
                .type(dto.getType())
                .capacity(dto.getCapacity())
                .location(dto.getLocation())
                .address(dto.getAddress())
                .pricePerMinute(dto.getPricePerMinute())
                .description(dto.getDescription())
                .photos(dto.getPhotos())
                .state(dto.getState())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
