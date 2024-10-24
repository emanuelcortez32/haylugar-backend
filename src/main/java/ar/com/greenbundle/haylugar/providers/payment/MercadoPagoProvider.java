package ar.com.greenbundle.haylugar.providers.payment;

import ar.com.greenbundle.haylugar.dto.UserPaymentCardDto;
import ar.com.greenbundle.haylugar.exceptions.PaymentProcessException;
import ar.com.greenbundle.haylugar.pojo.Customer;
import ar.com.greenbundle.haylugar.pojo.Payment;
import ar.com.greenbundle.haylugar.pojo.constants.Currency;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus;
import ar.com.greenbundle.haylugar.rest.clients.ThirdPartyClientResponse;
import ar.com.greenbundle.haylugar.rest.clients.mercadopago.MercadoPagoClient;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static ar.com.greenbundle.haylugar.pojo.constants.PaymentMethod.CREDIT_CARD;
import static ar.com.greenbundle.haylugar.pojo.constants.PaymentProvider.MERCADO_PAGO;

@AllArgsConstructor
public class MercadoPagoProvider implements PaymentProvider {
    private final MercadoPagoClient mercadoPagoClient;
    @Override
    public Payment createPayment(String customerId, String customerEmail, String cardToken, double amount) {
        ThirdPartyClientResponse createPayment = mercadoPagoClient.createPayment(customerId, customerEmail,
                cardToken, amount);

        if(!createPayment.isSuccess())
            throw new PaymentProcessException((String) createPayment.getData());

        com.mercadopago.resources.payment.Payment payment = (com.mercadopago.resources.payment.Payment) createPayment.getData();

        BigDecimal totalPaidAmount = payment.getTransactionDetails().getTotalPaidAmount();
        BigDecimal netReceivedAmount = payment.getTransactionDetails().getNetReceivedAmount();

        BigDecimal netAmountUser = calculateCommissionPercentage(totalPaidAmount, new BigDecimal("85"));
        BigDecimal amountProvider = totalPaidAmount.subtract(netReceivedAmount);
        BigDecimal amountPlatform = netReceivedAmount.subtract(netAmountUser);

        return Payment.builder()
                .id(payment.getId().toString())
                .provider(MERCADO_PAGO)
                .method(CREDIT_CARD)
                .paymentTypeId(payment.getPaymentTypeId())
                .paymentMethodId(payment.getPaymentMethodId())
                .currency(Currency.valueOf(payment.getCurrencyId()))
                .totalPrice(totalPaidAmount.doubleValue())
                .providerAmount(amountProvider.doubleValue())
                .platformAmount(amountPlatform.doubleValue())
                .userNetAmount(netReceivedAmount.doubleValue())
                .transactionAmountRefunded(payment.getTransactionAmountRefunded().doubleValue())
                .issuerId(payment.getIssuerId())
                .dateCreated(payment.getDateCreated())
                .dateApproved(payment.getDateApproved())
                .dateLastUpdated(payment.getDateLastUpdated())
                .dateOfExpiration(payment.getDateOfExpiration())
                .moneyReleaseDate(payment.getMoneyReleaseDate())
                .metadata(payment.getMetadata())
                .status(PaymentStatus.valueOf(payment.getStatus().toUpperCase()))
                .statusDetail(payment.getStatusDetail())
                .build();
    }

    @Override
    public Customer searchCustomer(String customerEmail) {
        ThirdPartyClientResponse searchCustomer = mercadoPagoClient.searchCustomerByEmail(customerEmail);

        if(!searchCustomer.isSuccess())
            throw new PaymentProcessException((String) searchCustomer.getData());

        if(searchCustomer.getData() == null)
            return null;

        com.mercadopago.resources.customer.Customer customer = (com.mercadopago.resources.customer.Customer) searchCustomer.getData();

        List<UserPaymentCardDto> cards = customer.getCards().stream()
                .map(card -> UserPaymentCardDto.builder()
                        .referenceId(card.getId())
                        .paymentMethod(card.getPaymentMethod().getName())
                        .paymentType(card.getPaymentMethod().getPaymentTypeId())
                        .issuerName(card.getIssuer().getName())
                        .issuerId(card.getIssuer().getId())
                        .securityCodeLength(card.getSecurityCode().getLength())
                        .securityCodeCardLocation(card.getSecurityCode().getCardLocation())
                        .expirationMonth(card.getExpirationMonth())
                        .expirationYear(card.getExpirationYear())
                        .cardDefault(card.getId().equals(customer.getDefaultCard()))
                        .build())
                .toList();

        return Customer.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .identificationType(customer.getIdentification().getType())
                .identificationNumber(customer.getIdentification().getNumber())
                .cards(cards)
                .build();
    }

    @Override
    public Customer createCustomer(String name, String lastName, String email, String dniNumber) {
        ThirdPartyClientResponse customerRequest = mercadoPagoClient.createCustomer(name, lastName, email, dniNumber);

        if(!customerRequest.isSuccess())
            throw new PaymentProcessException((String) customerRequest.getData());

        com.mercadopago.resources.customer.Customer customer = (com.mercadopago.resources.customer.Customer) customerRequest.getData();

        return Customer.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .identificationType(customer.getIdentification().getType())
                .identificationNumber(customer.getIdentification().getNumber())
                .build();
    }

    private BigDecimal calculateCommissionPercentage(BigDecimal value, BigDecimal percentage) {
        return value.multiply(percentage).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }
}
