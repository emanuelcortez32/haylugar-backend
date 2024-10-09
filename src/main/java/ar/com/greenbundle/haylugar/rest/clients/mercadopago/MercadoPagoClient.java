package ar.com.greenbundle.haylugar.rest.clients.mercadopago;

import ar.com.greenbundle.haylugar.rest.clients.ThirdPartyClientResponse;
import com.mercadopago.client.cardtoken.CardTokenClient;
import com.mercadopago.client.cardtoken.CardTokenRequest;
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
import com.mercadopago.resources.CardToken;
import com.mercadopago.resources.customer.Customer;
import com.mercadopago.resources.customer.CustomerCard;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Component
public class MercadoPagoClient {
    @Autowired
    private PaymentClient paymentClient;
    @Autowired
    private CustomerClient customerClient;
    @Autowired
    private CustomerCardClient customerCardClient;
    @Autowired
    private CardTokenClient cardTokenClient;

    public ThirdPartyClientResponse createCustomer(String email) {
        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
            CustomerRequest customerRequest = CustomerRequest.builder()
                    .email(email)
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
                Optional<Customer> customer = customerSearchResult.getResults()
                        .stream()
                        .filter(cuztomer -> cuztomer.getEmail().equals(customerEmail))
                        .findFirst();

                if(customer.isEmpty())
                    return createCustomer(customerEmail);

                thirdPartyClientResponse.setData(customer.get());
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

    public ThirdPartyClientResponse addCardCustomer(String customerId, String tokenCard) {
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

    public ThirdPartyClientResponse createPayment(String customerId, String customerEmail,
                                                               String cardToken, double amount,
                                                               String description) {
        final String STATEMENT_DESCRIPTOR = "MERCADOPAGO_HAYLUGAR";
        final int ONE_QUOTA = 1;

        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
            PaymentPayerRequest paymentPayerRequest = PaymentPayerRequest.builder()
                    .id(customerId)
                    .email(customerEmail)
                    .build();

            PaymentCreateRequest paymentRequest = PaymentCreateRequest.builder()
                    .token(cardToken)
                    .transactionAmount(new BigDecimal(amount))
                    .description(description)
                    .statementDescriptor(STATEMENT_DESCRIPTOR)
                    .installments(ONE_QUOTA)
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

    public ThirdPartyClientResponse createPayment(String customerId, String customerEmail,
                                                               String cardToken, double amount,
                                                               int quotasQuantity, String description) {
        final String STATEMENT_DESCRIPTOR = "MERCADOPAGO_HAYLUGAR";

        ThirdPartyClientResponse thirdPartyClientResponse = new ThirdPartyClientResponse();

        try {
            PaymentPayerRequest paymentPayerRequest = PaymentPayerRequest.builder()
                    .id(customerId)
                    .email(customerEmail)
                    .build();

            PaymentCreateRequest paymentRequest = PaymentCreateRequest.builder()
                    .token(cardToken)
                    .transactionAmount(new BigDecimal(amount))
                    .description(description)
                    .statementDescriptor(STATEMENT_DESCRIPTOR)
                    .installments(quotasQuantity)
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
}
