package ar.com.greenbundle.haylugar.config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.mercadopago.client.cardtoken.CardTokenClient;
import com.mercadopago.client.customer.CustomerCardClient;
import com.mercadopago.client.customer.CustomerClient;
import com.mercadopago.client.payment.PaymentClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfig implements InitializingBean {
    @Value("${mercadopago.secret.token}")
    private String mercadoPagoSecretToken;

    @Autowired
    private AWSSimpleSystemsManagement awsSimpleSystemsManagement;

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }

    @Bean
    public CustomerClient customerClient() {
        return new CustomerClient();
    }

    @Bean
    public CustomerCardClient customerCardClient() {
        return new CustomerCardClient();
    }

    @Bean
    public CardTokenClient cardTokenClient() {
        return new CardTokenClient();
    }

    @Override
    public void afterPropertiesSet() {
        GetParameterRequest getParameterRequest = new GetParameterRequest();

        getParameterRequest.withName(mercadoPagoSecretToken).setWithDecryption(true);

        GetParameterResult getParameterResult = awsSimpleSystemsManagement.getParameter(getParameterRequest);

        com.mercadopago.MercadoPagoConfig.setAccessToken(getParameterResult.getParameter().getValue());
    }
}
