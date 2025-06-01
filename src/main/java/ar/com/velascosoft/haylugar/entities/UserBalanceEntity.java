package ar.com.velascosoft.haylugar.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("user_balances")
public class UserBalanceEntity extends GenericEntity {
    @Column("user_id")
    private String userId;
    @Column("total_amount")
    private double totalAmount;
    @Column("amount_pending_to_withdraw")
    private double amountPendingToWithdraw;
    @Column("amount_available_to_withdraw")
    private double amountAvailableToWithdraw;
    @Column("available_minutes")
    private int availableMinutes;

    @Builder
    public UserBalanceEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, boolean deleted, String userId, double totalAmount, double amountPendingToWithdraw, double amountAvailableToWithdraw, int availableMinutes) {
        super(id, createdAt, updatedAt, version, deleted);
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.amountPendingToWithdraw = amountPendingToWithdraw;
        this.amountAvailableToWithdraw = amountAvailableToWithdraw;
        this.availableMinutes = availableMinutes;
    }
}
