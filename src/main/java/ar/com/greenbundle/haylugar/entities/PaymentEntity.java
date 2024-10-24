package ar.com.greenbundle.haylugar.entities;

import ar.com.greenbundle.haylugar.pojo.PaymentTransactionDetail;
import ar.com.greenbundle.haylugar.pojo.constants.Currency;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentMethod;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentProvider;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("payments")
public class PaymentEntity extends GenericEntity {
    @Column("reference_id")
    private String referenceId;
    private PaymentMethod method;
    private PaymentProvider provider;
    @Column("total_price")
    private double totalPrice;
    @Column("provider_amount")
    private double providerAmount;
    @Column("platform_amount")
    private double platformAmount;
    @Column("user_net_amount")
    private double userNetAmount;
    private Currency currency;
    @Column("last_status")
    private PaymentStatus lastStatus;
    @Column("transaction_details")
    private List<PaymentTransactionDetail> transactionDetails;

    @Builder
    public PaymentEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, String referenceId, PaymentMethod method, PaymentProvider provider, double totalPrice, double providerAmount, double platformAmount, double userNetAmount, Currency currency, PaymentStatus lastStatus, List<PaymentTransactionDetail> transactionDetails) {
        super(id, createdAt, updatedAt, version);
        this.referenceId = referenceId;
        this.method = method;
        this.provider = provider;
        this.totalPrice = totalPrice;
        this.providerAmount = providerAmount;
        this.platformAmount = platformAmount;
        this.userNetAmount = userNetAmount;
        this.currency = currency;
        this.lastStatus = lastStatus;
        this.transactionDetails = transactionDetails;
    }
}
