package ar.com.velascosoft.haylugar.providers.payment;

import ar.com.velascosoft.haylugar.dto.UserPaymentCardDto;
import ar.com.velascosoft.haylugar.exceptions.PaymentProcessException;
import ar.com.velascosoft.haylugar.pojo.Customer;
import ar.com.velascosoft.haylugar.pojo.Payment;
import ar.com.velascosoft.haylugar.pojo.constants.Currency;
import ar.com.velascosoft.haylugar.pojo.constants.PaymentStatus;
import ar.com.velascosoft.haylugar.rest.clients.ThirdPartyClientResponse;
import ar.com.velascosoft.haylugar.rest.clients.mercadopago.MercadoPagoClient;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.preference.Preference;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@AllArgsConstructor
public class MercadoPagoProvider implements PaymentProvider {
    private final MercadoPagoClient mercadoPagoClient;

    @Override
    public Payment createPayment(String customerId, String customerEmail, double amount, String description) {
        ThirdPartyClientResponse<Preference> createPreference = mercadoPagoClient
                .createPreference(description, customerEmail, amount);

        if(!createPreference.isSuccess())
            throw new PaymentProcessException("Payment could not be created");

        Preference preference = createPreference.getData();

        return Payment.builder().id(preference.getId())
                .provider(ar.com.velascosoft.haylugar.pojo.constants.PaymentProvider.MERCADO_PAGO)
                .currency(Currency.ARS)
                .status(PaymentStatus.PENDING)
                .statusDetail("PAYMENT_CRATED")
                .dateCreated(preference.getDateCreated())
                .dateOfExpiration(preference.getDateOfExpiration())
                .metadata(preference.getMetadata())
                .initUrl(preference.getSandboxInitPoint())
                .build();
    }

    @Override
    public Customer searchCustomer(String customerEmail) {
        ThirdPartyClientResponse<com.mercadopago.resources.customer.Customer> searchCustomer = mercadoPagoClient
                .searchCustomerByEmail(customerEmail);

        if(!searchCustomer.isSuccess())
            throw new PaymentProcessException("An error occurred while trying to search for the customer");

        if(searchCustomer.getData() == null)
            return null;

        com.mercadopago.resources.customer.Customer customer = searchCustomer.getData();

        List<UserPaymentCardDto> cards = customer.getCards().stream()
                .map(card -> UserPaymentCardDto.builder()
                        .externalReferenceId(card.getId())
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
        ThirdPartyClientResponse<com.mercadopago.resources.customer.Customer> customerRequest = mercadoPagoClient
                .createCustomer(name, lastName, email, dniNumber);

        if(!customerRequest.isSuccess())
            throw new PaymentProcessException("An error occurred while trying to create customer");

        com.mercadopago.resources.customer.Customer customer = customerRequest.getData();

        return Customer.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .identificationType(customer.getIdentification().getType())
                .identificationNumber(customer.getIdentification().getNumber())
                .build();
    }

    @Override
    public Payment updatePayment(Payment payment) {
        return null;
    }

    @Override
    public Payment getPayment(String paymentId) {
        /*
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
                .build();*/
        return null;
    }

    @Override
    public Payment getPaymentByOrder(Long orderId) {
        ThirdPartyClientResponse<MerchantOrder> getOrder = mercadoPagoClient
                .getMerchantOrder(orderId);

        if(!getOrder.isSuccess())
            throw new PaymentProcessException("Payment order could not be found");

        MerchantOrder order = getOrder.getData();
        BigDecimal totalPaidAmount = order.getTotalAmount();
        BigDecimal netReceivedAmount = order.getTotalAmount();

        BigDecimal netAmountUser = calculateCommissionPercentage(totalPaidAmount, new BigDecimal("85"));
        BigDecimal amountProvider = totalPaidAmount.subtract(netReceivedAmount);
        BigDecimal amountPlatform = netReceivedAmount.subtract(netAmountUser);

        return null;
    }

    private BigDecimal calculateCommissionPercentage(BigDecimal value, BigDecimal percentage) {
        return value.multiply(percentage).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }
}
