package ar.com.greenbundle.haylugar.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
