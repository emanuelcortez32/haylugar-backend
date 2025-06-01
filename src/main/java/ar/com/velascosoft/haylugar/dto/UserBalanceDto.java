package ar.com.velascosoft.haylugar.dto;

import ar.com.velascosoft.haylugar.entities.UserBalanceEntity;
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
public class UserBalanceDto extends EntityDto<UserBalanceEntity, UserBalanceDto> {
    private UserDto user;
    private double totalAmount;
    private double amountPendingToWithdraw;
    private double amountAvailableToWithdraw;
    private int availableMinutes;

    @Builder
    public UserBalanceDto(String id, LocalDateTime createdAt, Long version, UserDto user, double totalAmount, double amountPendingToWithdraw, double amountAvailableToWithdraw, int availableMinutes) {
        super(id, createdAt, version);
        this.user = user;
        this.totalAmount = totalAmount;
        this.amountPendingToWithdraw = amountPendingToWithdraw;
        this.amountAvailableToWithdraw = amountAvailableToWithdraw;
        this.availableMinutes = availableMinutes;
    }

    @Override
    public UserBalanceDto dtoFromEntity(UserBalanceEntity entity) {
        return UserBalanceDto.builder()
                .id(entity.getId())
                .user(UserDto.builder().id(entity.getUserId()).build())
                .totalAmount(entity.getTotalAmount())
                .amountPendingToWithdraw(entity.getAmountPendingToWithdraw())
                .amountAvailableToWithdraw(entity.getAmountAvailableToWithdraw())
                .availableMinutes(entity.getAvailableMinutes())
                .build();
    }

    @Override
    public UserBalanceEntity dtoToEntity(UserBalanceDto dto) {
        return UserBalanceEntity.builder()
                .id(dto.getId())
                .userId(dto.getUser().getId())
                .totalAmount(dto.getTotalAmount())
                .amountPendingToWithdraw(dto.getAmountPendingToWithdraw())
                .amountAvailableToWithdraw(dto.getAmountAvailableToWithdraw())
                .availableMinutes(dto.getAvailableMinutes())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
