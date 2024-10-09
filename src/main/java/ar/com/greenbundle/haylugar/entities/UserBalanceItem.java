package ar.com.greenbundle.haylugar.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Document("balances")
public class UserBalanceItem extends GenericItem {
    private String userId;
    private double totalAmount;
    private double amountPendingToWithdraw;
    private double amountAvailableToWithdraw;
}
