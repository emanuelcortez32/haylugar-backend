package ar.com.greenbundle.haylugar.config;

import ar.com.greenbundle.haylugar.providers.payment.FakeProvider;
import ar.com.greenbundle.haylugar.providers.payment.MercadoPagoProvider;
import ar.com.greenbundle.haylugar.providers.payment.PaymentProvider;
import ar.com.greenbundle.haylugar.rest.clients.mercadopago.MercadoPagoClient;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.mercadopago.client.cardtoken.CardTokenClient;
import com.mercadopago.client.customer.CustomerCardClient;
import com.mercadopago.client.customer.CustomerClient;
import com.mercadopago.client.payment.PaymentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class PaymentProviderConfig {
    @Value("${mercadopago.secret.token}")
    private String mercadoPagoSecretToken;

    @Autowired
    private AWSSimpleSystemsManagement awsSimpleSystemsManagement;

    @Profile({"local"})
    @Bean(name = "fakeProvider")
    public PaymentProvider fakeProvider() {
        return new FakeProvider();
    }

    @Profile({"dev","stage","prod"})
    @Bean(name = "mercadoPagoProvider")
    public PaymentProvider mercadoPagoProvider() {
        try {
            GetParameterRequest getParameterRequest = new GetParameterRequest();

            getParameterRequest.withName(mercadoPagoSecretToken).setWithDecryption(true);

            GetParameterResult getParameterResult = awsSimpleSystemsManagement.getParameter(getParameterRequest);

            com.mercadopago.MercadoPagoConfig.setAccessToken(getParameterResult.getParameter().getValue());

            MercadoPagoClient mercadoPagoClient = new MercadoPagoClient(
                    new PaymentClient(),
                    new CustomerClient(),
                    new CustomerCardClient(),
                    new CardTokenClient());

            return new MercadoPagoProvider(mercadoPagoClient);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
