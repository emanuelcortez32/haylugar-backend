package ar.com.greenbundle.haylugar.providers.payment;

import ar.com.greenbundle.haylugar.pojo.Customer;
import ar.com.greenbundle.haylugar.pojo.Payment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ar.com.greenbundle.haylugar.pojo.constants.Currency.ARS;
import static ar.com.greenbundle.haylugar.pojo.constants.PaymentMethod.CREDIT_CARD;
import static ar.com.greenbundle.haylugar.pojo.constants.PaymentProvider.FAKE;
import static ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus.APPROVED;

public class FakeProvider implements PaymentProvider {
    @Override
    public Payment createPayment(String customerId, String customerEmail, String cardToken, double amount) {
        double totalPrice = amount;
        double providerAmount = ((double) 3 / 100) * totalPrice;
        double netReceivedAmount = totalPrice - providerAmount;
        double userNetAmount = ((double) 80 / 100) * totalPrice;
        double platformAmount = netReceivedAmount - userNetAmount;


        return Payment.builder()
                .id(UUID.randomUUID().toString())
                .method(CREDIT_CARD)
                .provider(FAKE)
                .paymentTypeId("visa")
                .paymentMethodId("credit_card")
                .currency(ARS)
                .totalPrice(totalPrice)
                .providerAmount(providerAmount)
                .platformAmount(platformAmount)
                .userNetAmount(userNetAmount)
                .transactionAmountRefunded(0)
                .issuerId("111")
                .dateCreated(OffsetDateTime.now())
                .dateApproved(OffsetDateTime.now())
                .dateLastUpdated(OffsetDateTime.now())
                .dateOfExpiration(OffsetDateTime.now().plusDays(2))
                .moneyReleaseDate(null)
                .metadata(Map.of("some","data"))
                .status(APPROVED)
                .statusDetail("ok")
                .build();
    }

    @Override
    public Customer searchCustomer(String customerEmail) {
        return Customer.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Usancio")
                .lastName("Testero")
                .email("test@example.com")
                .identificationType("DNI")
                .identificationNumber("1111111")
                .cards(List.of())
                .build();
    }

    @Override
    public Customer createCustomer(String name, String lastName, String email, String dniNumber) {
        return Customer.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Usancio")
                .lastName("Testero")
                .email("test@example.com")
                .identificationType("DNI")
                .identificationNumber("1111111")
                .cards(List.of())
                .build();
    }
}
