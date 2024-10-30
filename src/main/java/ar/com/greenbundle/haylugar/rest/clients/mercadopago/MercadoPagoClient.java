package ar.com.greenbundle.haylugar.rest.clients.mercadopago;

import ar.com.greenbundle.haylugar.rest.clients.ThirdPartyClientResponse;
import com.mercadopago.client.cardtoken.CardTokenClient;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.customer.CustomerCardClient;
import com.mercadopago.client.customer.CustomerClient;
import com.mercadopago.client.customer.CustomerRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.net.MPResultsResourcesPage;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.customer.Customer;
import com.mercadopago.resources.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class MercadoPagoClient {
    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;
    private final CustomerCardClient customerCardClient;
    private final CardTokenClient cardTokenClient;

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

    public ThirdPartyClientResponse<Payment> createPayment(String customerId, String customerEmail,
                                                  String cardToken, double amount) {
        final String DESCRIPTION = "Pago por uso de HayLugar Spot";
        final String STATEMENT_DESCRIPTOR = "MERCADOPAGO_HAYLUGAR";
        final int QUOTAS = 1;

        return executeClientRequest(() -> {
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

            return paymentClient.create(paymentRequest);
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
