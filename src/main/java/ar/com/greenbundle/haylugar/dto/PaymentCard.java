package ar.com.greenbundle.haylugar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCard {
    private String id;
    private String securityCode;
    private boolean cardDefault;
}
