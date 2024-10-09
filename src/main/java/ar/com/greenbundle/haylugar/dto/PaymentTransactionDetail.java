package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.dto.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionDetail {
    private Date date;
    private PaymentStatus status;
}
