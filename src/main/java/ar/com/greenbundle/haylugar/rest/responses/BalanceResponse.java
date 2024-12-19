package ar.com.greenbundle.haylugar.rest.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceResponse {
    private String id;
    private double totalAmount;
    private double amountPendingToWithdraw;
    private double amountAvailableToWithdraw;
    private int availableMinutes;
}
