package ar.com.velascosoft.haylugar.dto;

import ar.com.velascosoft.haylugar.entities.UserProfileEntity;
import ar.com.velascosoft.haylugar.pojo.constants.Gender;
import ar.com.velascosoft.haylugar.pojo.constants.Nationality;
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
public class UserProfileDto extends EntityDto<UserProfileEntity, UserProfileDto> {
    private String userId;
    private String name;
    private String lastName;
    private String dni;
    private Gender gender;
    private String birthDate;
    private Nationality nationality;
    private UserPaymentProfileDto paymentProfile;

    @Builder
    public UserProfileDto(String id, LocalDateTime createdAt, Long version, String userId, String name, String lastName, String dni, Gender gender, String birthDate, Nationality nationality, UserPaymentProfileDto paymentProfile) {
        super(id, createdAt, version);
        this.userId = userId;
        this.name = name;
        this.lastName = lastName;
        this.dni = dni;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.paymentProfile = paymentProfile;
    }

    @Override
    public UserProfileDto dtoFromEntity(UserProfileEntity entity) {
        return UserProfileDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .lastName(entity.getSurname())
                .dni(entity.getDni())
                .birthDate(entity.getBirthDate())
                .gender(entity.getGender())
                .nationality(entity.getNationality())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }

    @Override
    public UserProfileEntity dtoToEntity(UserProfileDto dto) {
        return UserProfileEntity.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .name(dto.getName())
                .surname(dto.getLastName())
                .nationality(dto.getNationality())
                .gender(dto.getGender())
                .dni(dto.getDni())
                .birthDate(dto.getBirthDate())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
