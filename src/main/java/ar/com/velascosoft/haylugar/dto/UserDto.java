package ar.com.velascosoft.haylugar.dto;

import ar.com.velascosoft.haylugar.entities.UserEntity;
import ar.com.velascosoft.haylugar.pojo.constants.UserRole;
import ar.com.velascosoft.haylugar.pojo.constants.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends EntityDto<UserEntity, UserDto> {
    private String email;
    private String password;
    private UserState state;
    private List<UserRole> roles;
    private UserProfileDto profile;

    @Builder
    public UserDto(String id, LocalDateTime createdAt, Long version, String email, String password, UserState state, List<UserRole> roles, UserProfileDto profile) {
        super(id, createdAt, version);
        this.email = email;
        this.password = password;
        this.state = state;
        this.roles = roles;
        this.profile = profile;
    }

    @Override
    public UserDto dtoFromEntity(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .state(entity.getState())
                .roles(entity.getRoles())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }

    @Override
    public UserEntity dtoToEntity(UserDto dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .state(dto.getState())
                .roles(dto.getRoles())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
