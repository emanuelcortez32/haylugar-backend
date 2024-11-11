package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.entities.UserVehicleEntity;
import ar.com.greenbundle.haylugar.pojo.constants.VehicleSize;
import ar.com.greenbundle.haylugar.pojo.constants.VehicleType;
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
public class UserVehicleDto extends EntityDto {
    private UserDto user;
    private String brand;
    private String model;
    private String patent;
    private String year;
    private VehicleType type;
    private VehicleSize size;
    private boolean defaultVehicle;

    @Builder
    public UserVehicleDto(String id, LocalDateTime createdAt, Long version, UserDto user, String brand, String model, String patent, String year, VehicleType type, VehicleSize size, boolean defaultVehicle) {
        super(id, createdAt, version);
        this.user = user;
        this.brand = brand;
        this.model = model;
        this.patent = patent;
        this.year = year;
        this.type = type;
        this.size = size;
        this.defaultVehicle = defaultVehicle;
    }

    public static UserVehicleDto.UserVehicleDtoBuilder builderFromEntity(UserVehicleEntity entity) {
        return UserVehicleDto.builder()
                .id(entity.getId())
                .user(UserDto.builder().id(entity.getUserId()).build())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .patent(entity.getPatent())
                .year(entity.getYear())
                .type(entity.getType())
                .size(entity.getSize())
                .defaultVehicle(entity.isDefaultVehicle())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion());
    }

    public static UserVehicleEntity mapToEntity(UserVehicleDto dto) {
        return UserVehicleEntity.builder()
                .id(dto.getId())
                .userId(dto.getUser().getId())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .patent(dto.getPatent())
                .year(dto.getYear())
                .type(dto.getType())
                .size(dto.getSize())
                .defaultVehicle(dto.isDefaultVehicle())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
