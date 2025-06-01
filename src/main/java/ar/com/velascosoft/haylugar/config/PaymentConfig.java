package ar.com.velascosoft.haylugar.config;

import ar.com.velascosoft.haylugar.providers.payment.FakeProvider;
import ar.com.velascosoft.haylugar.providers.payment.MercadoPagoProvider;
import ar.com.velascosoft.haylugar.providers.payment.PaymentProvider;
import ar.com.velascosoft.haylugar.rest.clients.mercadopago.MercadoPagoClient;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.client.customer.CustomerClient;
import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

@Configuration
public class PaymentConfig {
    @Value("${mercadopago.secret.token}")
    private String mercadoPagoSecretToken;

    @Autowired
    private AWSSimpleSystemsManagement awsSimpleSystemsManagement;

    @Autowired
    private SecretsManagerClient awsSecretsManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Profile({"local-dev"})
    @Bean(name = "fakeProvider")
    public PaymentProvider fakeProvider() {
        return new FakeProvider();
    }

    @Profile({"local", "dev","stage","prod"})
    @Bean(name = "mercadoPagoProvider")
    public PaymentProvider mercadoPagoProvider() {
        try {
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(mercadoPagoSecretToken)
                    .build();

            GetSecretValueResponse getSecretValueResponse = awsSecretsManager.getSecretValue(getSecretValueRequest);

            Map<String, String> secretData = objectMapper.readValue(getSecretValueResponse.secretString(), new TypeReference<>() {});

            com.mercadopago.MercadoPagoConfig.setAccessToken(secretData.get("accessToken"));

            MercadoPagoClient mercadoPagoClient = new MercadoPagoClient(
                    new PaymentClient(),
                    new CustomerClient(),
                    new PreferenceClient(),
                    new MerchantOrderClient());

            return new MercadoPagoProvider(mercadoPagoClient);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
