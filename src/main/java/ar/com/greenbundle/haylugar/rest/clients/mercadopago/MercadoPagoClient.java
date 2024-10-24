package ar.com.greenbundle.haylugar.rest.clients.mercadopago;

import ar.com.greenbundle.haylugar.rest.clients.ThirdPartyClientResponse;
import com.mercadopago.client.cardtoken.CardTokenClient;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.customer.CustomerCardClient;
import com.mercadopago.client.customer.CustomerCardCreateRequest;
import com.mercadopago.client.customer.CustomerClient;
import com.mercadopago.client.customer.CustomerRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPResultsResourcesPage;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.customer.Customer;
import com.mercadopago.resources.customer.CustomerCard;
import com.mercadopago.resources.payment.Payment;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Map;
@AllArgsConstructor
public class MercadoPagoClient {
    private final PaymentClient paymentClient;
    private final CustomerClient customerClient;
    private final CustomerCardClient customerCardClient;
    private final CardTokenClient cardTokenClient;

    public ThirdPartyClientResponse createCustomer(String name, String lastName, String email,
                                                   String dniNumber) {
        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
            CustomerRequest customerRequest = CustomerRequest.builder()
                    .email(email)
                    .firstName(name)
                    .lastName(lastName)
                    .identification(IdentificationRequest.builder()
                            .type("DNI")
                            .number(dniNumber)
                            .build())
                    .build();

            Customer customer = customerClient.create(customerRequest);

            thirdPartyClientResponse.setSuccess(true);
            thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
            thirdPartyClientResponse.setData(customer);
        } catch (MPApiException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.PAYMENT_REQUIRED);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent()));

        } catch (MPException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Content: %s%n",
                    ex.getMessage()));
        }

        return thirdPartyClientResponse;
    }

    public ThirdPartyClientResponse searchCustomerByEmail(String customerEmail) {
        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try{
            thirdPartyClientResponse.setSuccess(true);
            thirdPartyClientResponse.setStatusCode(HttpStatus.OK);

            MPSearchRequest searchRequest = MPSearchRequest.builder()
                    .limit(10)
                    .offset(10)
                    .filters(Map.of("email", customerEmail))
                    .build();

            MPResultsResourcesPage<Customer> customerSearchResult = customerClient.search(searchRequest);

            if(customerSearchResult.getResults().size() == 1) {
                thirdPartyClientResponse.setData(customerSearchResult.getResults().get(0));
            } else {
                thirdPartyClientResponse.setData(customerSearchResult.getResults()
                        .stream()
                        .filter(customer -> customer.getEmail().equals(customerEmail))
                        .findFirst()
                        .orElse(null));
            }

        } catch (MPApiException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.PAYMENT_REQUIRED);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent()));

        } catch (MPException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Content: %s%n",
                    ex.getMessage()));
        }

        return thirdPartyClientResponse;
    }

    public ThirdPartyClientResponse addCardToCustomer(String customerId, String tokenCard) {
        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
            CustomerCardCreateRequest customerCardCreateRequest = CustomerCardCreateRequest.builder()
                    .token(tokenCard)
                    .build();

            CustomerCard customerCard = customerCardClient.create(customerId, customerCardCreateRequest);

            thirdPartyClientResponse.setSuccess(true);
            thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
            thirdPartyClientResponse.setData(customerCard);

        } catch (MPApiException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.PAYMENT_REQUIRED);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent()));

        } catch (MPException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Content: %s%n",
                    ex.getMessage()));
        }

        return thirdPartyClientResponse;
    }

    public ThirdPartyClientResponse createPayment(String customerId, String customerEmail,
                                                  String cardToken, double amount) {
        final String DESCRIPTION = "Pago por uso de HayLugar Spot";
        final String STATEMENT_DESCRIPTOR = "MERCADOPAGO_HAYLUGAR";
        final int QUOTAS = 1;

        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
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

            Payment payment = paymentClient.create(paymentRequest);

            thirdPartyClientResponse.setSuccess(true);
            thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
            thirdPartyClientResponse.setData(payment);

        } catch (MPApiException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.PAYMENT_REQUIRED);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent()));

        } catch (MPException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Content: %s%n",
                    ex.getMessage()));
        }

        return thirdPartyClientResponse;
    }

    /*

    public ThirdPartyClientResponse getCustomer(String customerId) {
        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
            Customer customer = customerClient.get(customerId);

            thirdPartyClientResponse.setSuccess(true);
            thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
            thirdPartyClientResponse.setData(customer);

        } catch (MPApiException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.PAYMENT_REQUIRED);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent()));

        } catch (MPException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Content: %s%n",
                    ex.getMessage()));
        }

        return thirdPartyClientResponse;
    }

    public ThirdPartyClientResponse getCardCustomer(String customerId, String cardId) {
        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
            CustomerCard customerCard = customerCardClient.get(customerId, cardId);

            thirdPartyClientResponse.setSuccess(true);
            thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
            thirdPartyClientResponse.setData(customerCard);

        } catch (MPApiException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.PAYMENT_REQUIRED);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent()));

        } catch (MPException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Content: %s%n",
                    ex.getMessage()));
        }

        return thirdPartyClientResponse;
    }

    public ThirdPartyClientResponse createCardToken(String customerId, String cardId, String cardSecurityCode) {
        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {

            CardTokenRequest cardTokenRequest = CardTokenRequest.builder()
                    .cardId(cardId)
                    .customerId(customerId)
                    .securityCode(cardSecurityCode)
                    .build();

            CardToken cardToken = cardTokenClient.create(cardTokenRequest);

            thirdPartyClientResponse.setSuccess(true);
            thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
            thirdPartyClientResponse.setData(cardToken);

        } catch (MPApiException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.PAYMENT_REQUIRED);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent()));

        } catch (MPException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Content: %s%n",
                    ex.getMessage()));
        }

        return thirdPartyClientResponse;
    }

    public ThirdPartyClientResponse getCardToken(String cardTokenId) {
        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
            CardToken cardToken = cardTokenClient.get(cardTokenId);

            thirdPartyClientResponse.setSuccess(true);
            thirdPartyClientResponse.setStatusCode(HttpStatus.OK);
            thirdPartyClientResponse.setData(cardToken);

        } catch (MPApiException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.PAYMENT_REQUIRED);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Status: %s, Content: %s%n",
                    ex.getApiResponse().getStatusCode(), ex.getApiResponse().getContent()));

        } catch (MPException ex) {
            thirdPartyClientResponse.setSuccess(false);
            thirdPartyClientResponse.setStatusCode(HttpStatus.BAD_GATEWAY);
            thirdPartyClientResponse.setData(String.format("MercadoPago Error. Content: %s%n",
                    ex.getMessage()));
        }

        return thirdPartyClientResponse;
    }



     */
}
