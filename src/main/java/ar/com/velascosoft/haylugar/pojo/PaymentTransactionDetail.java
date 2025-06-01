package ar.com.velascosoft.haylugar.pojo;

import ar.com.velascosoft.haylugar.pojo.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionDetail {
    private OffsetDateTime date;
    private PaymentStatus status;
    private String statusDetail;
}
