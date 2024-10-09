package ar.com.greenbundle.haylugar.entities;

import ar.com.greenbundle.haylugar.dto.PaymentTransactionDetail;
import ar.com.greenbundle.haylugar.dto.constants.Currency;
import ar.com.greenbundle.haylugar.dto.constants.PaymentMethod;
import ar.com.greenbundle.haylugar.dto.constants.PaymentProvider;
import ar.com.greenbundle.haylugar.dto.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ar.com.greenbundle.haylugar.dto.constants.Currency.ARS;
import static ar.com.greenbundle.haylugar.dto.constants.PaymentStatus.CREATED;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("payments")
public class PaymentItem extends GenericItem {
    private PaymentMethod method;
    private PaymentProvider provider;
    @Builder.Default
    private double totalPrice = 0.0;
    private double providerAmount;
    private double platformAmount;
    private double userNetAmount;
    @Builder.Default
    private Currency currency = ARS;
    @Builder.Default
    private PaymentStatus lastStatus = CREATED;
    @Builder.Default
    private List<PaymentTransactionDetail> transactionDetails = new ArrayList<>(Collections.singletonList(
            PaymentTransactionDetail.builder().status(CREATED).build()));
}
