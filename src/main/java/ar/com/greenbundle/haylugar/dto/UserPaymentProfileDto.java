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
public class UserPaymentProfileDto extends EntityDto {
    private String userId;
    private String customerId;
    private List<UserPaymentCardDto> cards;
    @Builder
    public UserPaymentProfileDto(String id, LocalDateTime createdAt, Long version, String userId, String customerId, List<UserPaymentCardDto> cards) {
        super(id, createdAt, version);
        this.userId = userId;
        this.customerId = customerId;
        this.cards = cards;
    }

    public static UserPaymentProfileDto.UserPaymentProfileDtoBuilder builderFromEntity(UserPaymentProfileEntity entity) {
        return UserPaymentProfileDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .customerId(entity.getCustomerId())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion());
    }

    public static UserPaymentProfileEntity mapToEntity(UserPaymentProfileDto dto) {
        return UserPaymentProfileEntity.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .customerId(dto.getCustomerId())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
