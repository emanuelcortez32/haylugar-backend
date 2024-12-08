package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.entities.UserPaymentProfileEntity;
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
public class UserPaymentProfileDto extends EntityDto<UserPaymentProfileEntity, UserPaymentProfileDto> {
    private UserDto user;
    private String externalReferenceId;
    private List<UserPaymentCardDto> cards;
    @Builder
    public UserPaymentProfileDto(String id, LocalDateTime createdAt, Long version, UserDto user, String externalReferenceId, List<UserPaymentCardDto> cards) {
        super(id, createdAt, version);
        this.user = user;
        this.externalReferenceId = externalReferenceId;
        this.cards = cards;
    }

    @Override
    public UserPaymentProfileDto dtoFromEntity(UserPaymentProfileEntity entity) {
        return UserPaymentProfileDto.builder()
                .id(entity.getId())
                .user(UserDto.builder().id(entity.getUserId()).build())
                .externalReferenceId(entity.getExternalReferenceId())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }

    @Override
    public UserPaymentProfileEntity dtoToEntity(UserPaymentProfileDto dto) {
        return UserPaymentProfileEntity.builder()
                .id(dto.getId())
                .userId(dto.getUser().getId())
                .externalReferenceId(dto.getExternalReferenceId())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
