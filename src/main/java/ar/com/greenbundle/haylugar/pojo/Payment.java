package ar.com.greenbundle.haylugar.pojo;

import ar.com.greenbundle.haylugar.pojo.constants.Currency;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentMethod;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentProvider;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    private String id;
    private PaymentMethod method;
    private PaymentProvider provider;
    private String issuerId;
    private String paymentMethodId;
    private String paymentTypeId;
    private double totalPrice;
    private double providerAmount;
    private double platformAmount;
    private double userNetAmount;
    private double transactionAmountRefunded;
    private Currency currency;
    private PaymentStatus status;
    private String statusDetail;
    private OffsetDateTime dateCreated;
    private OffsetDateTime dateApproved;
    private OffsetDateTime dateLastUpdated;
    private OffsetDateTime dateOfExpiration;
    private OffsetDateTime moneyReleaseDate;
    private Map<String, Object> metadata;
}
