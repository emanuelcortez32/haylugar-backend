package ar.com.greenbundle.haylugar.rest.clients.mercadopago;

import ar.com.greenbundle.haylugar.pojo.constants.Currency;
import ar.com.greenbundle.haylugar.rest.clients.ThirdPartyClientResponse;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.customer.CustomerClient;
import com.mercadopago.client.customer.CustomerRequest;
import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferencePaymentTypeRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.net.MPResultsResourcesPage;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.customer.Customer;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class MercadoPagoClient {
    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;
    private final PreferenceClient preferenceClient;
    private final MerchantOrderClient merchantOrderClient;

    public ThirdPartyClientResponse<Customer> createCustomer(String name, String lastName, String email,
                                                   String dniNumber) {
        return executeClientRequest(() -> {
            CustomerRequest customerRequest = CustomerRequest.builder()
                    .email(email)
                    .firstName(name)
                    .lastName(lastName)
                    .identification(IdentificationRequest.builder()
                            .type("DNI")
                            .number(dniNumber)
                            .build())
                    .build();

            return customerClient.create(customerRequest);
        }, new ThirdPartyClientResponse<>());
    }

    public ThirdPartyClientResponse<Customer> searchCustomerByEmail(String customerEmail) {

        return executeClientRequest(() -> {
            MPSearchRequest searchRequest = MPSearchRequest.builder()
                    .limit(10)
                    .offset(10)
                    .filters(Map.of("email", customerEmail))
                    .build();

            MPResultsResourcesPage<Customer> customerSearchResult = customerClient.search(searchRequest);

            if(customerSearchResult.getResults().size() == 1) {
                return customerSearchResult.getResults().get(0);
            } else {
                return customerSearchResult.getResults()
                        .stream()
                        .filter(customer -> customer.getEmail().equals(customerEmail))
                        .findFirst()
                        .orElse(null);
            }

        }, new ThirdPartyClientResponse<>());
    }

    public ThirdPartyClientResponse<Preference> createPreference(String customerEmail, double amount) {
        final String DESCRIPTION = "Compra de Minutos HayLugar!";
        final String STATEMENT_DESCRIPTOR = "MERCADOPAGO_HAYLUGAR";

        return executeClientRequest(() -> {
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(Map.of("X-Idempotency-Key", UUID.randomUUID().toString()))
                    .build();

            PreferencePayerRequest preferencePayerRequest = PreferencePayerRequest.builder()
                    .email("user@user.com.ar")
                    .build();

            PreferenceBackUrlsRequest backUrlsRequest = PreferenceBackUrlsRequest.builder()
                    .success("haylugar://payment/success")
                    .failure("haylugar://payment/failure")
                    .pending("haylugar://payment/pending")
                    .build();

            PreferencePaymentMethodsRequest paymentMethodsRequests = PreferencePaymentMethodsRequest.builder()
                    .excludedPaymentTypes(List.of(
                            PreferencePaymentTypeRequest.builder()
                                    .id("ticket")
                                    .build()
                    ))
                    .installments(1)
                    .build();

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(UUID.randomUUID().toString())
                    .title(DESCRIPTION)
                    .categoryId("services")
                    .quantity(1)
                    .currencyId(Currency.ARS.name())
                    .unitPrice(new BigDecimal(amount))
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(Collections.singletonList(itemRequest))
                    .binaryMode(false)
                    .statementDescriptor(STATEMENT_DESCRIPTOR)
                    .autoReturn("all")
                    .expires(true)
                    .dateOfExpiration(OffsetDateTime.now().plusDays(3))
                    .payer(preferencePayerRequest)
                    .paymentMethods(paymentMethodsRequests)
                    .backUrls(backUrlsRequest)
                    .build();

            return preferenceClient.create(preferenceRequest, requestOptions);

        }, new ThirdPartyClientResponse<>());
    }

    public ThirdPartyClientResponse<Preference> getPreference(String preferenceId) {
        return executeClientRequest(() -> preferenceClient.get(preferenceId), new ThirdPartyClientResponse<>());
    }

    public ThirdPartyClientResponse<MerchantOrder> getMerchantOrder(Long merchantOrderId) {
        return executeClientRequest(() -> merchantOrderClient.get(merchantOrderId), new ThirdPartyClientResponse<>());
    }

    public ThirdPartyClientResponse<Payment> createPayment(String customerId, String customerEmail,
                                                  String cardToken, double amount) {
        final String DESCRIPTION = "Pago por uso de HayLugar Spot";
        final String STATEMENT_DESCRIPTOR = "MERCADOPAGO_HAYLUGAR";
        final int QUOTAS = 1;

        return executeClientRequest(() -> {
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(Map.of("X-Idempotency-Key", UUID.randomUUID().toString()))
                    .build();

            PaymentPayerRequest paymentPayerRequest = PaymentPayerRequest.builder()
                    .id(customerId)
                    .email(customerEmail)
                    .build();

            PaymentCreateRequest paymentRequest = PaymentCreateRequest.builder()
                    .token(cardToken)
                    .transactionAmount(new BigDecimal(amount))
                    .description(DESCRIPTION)
                    .statementDescriptor(STATEMENT_DESCRIPTOR)
                    .installments(QUOTAS)
                    .binaryMode(true)
                    .payer(paymentPayerRequest)
                    .build();

            return paymentClient.create(paymentRequest, requestOptions);
        }, new ThirdPartyClientResponse<>());
    }


    private <T> ThirdPartyClientResponse<T> executeClientRequest(Callable<T> request, ThirdPartyClientResponse<T> response) {
        try {
            T result = request.call();
            response.setSuccess(true);
            response.setStatusCode(HttpStatus.OK);
            response.setData(result);

        } catch (MPApiException ex) {
            log.error("m=executeClientRequest : MPApiException Error ! [{}] [{}]", ex.getStatusCode(), ex.getMessage());

            response.setSuccess(false);
            response.setStatusCode(HttpStatus.valueOf(ex.getStatusCode()));

        } catch (Exception ex) {
            log.error("m=executeClientRequest : A unknown Error occurred ! [{}]", ex.getMessage());
            response.setSuccess(false);
            response.setStatusCode(HttpStatus.BAD_GATEWAY);
        }
        return response;
    }

}
