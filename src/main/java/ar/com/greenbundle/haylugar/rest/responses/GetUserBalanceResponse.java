package ar.com.greenbundle.haylugar.rest.responses;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUserBalanceResponse extends ApiResponse {

    @Builder
    public GetUserBalanceResponse(boolean success,
                                  String message,
                                  BalanceResponse balanceResponse) {
        super(success, message);
        this.data = Map.of("balance", balanceResponse);
    }
}
