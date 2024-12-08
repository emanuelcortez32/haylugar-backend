package ar.com.greenbundle.haylugar.config.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;

import java.net.URI;

@Configuration
public class SecretManagerConfig {
    @Value("${aws.region}")
    private String region;
    @Value("${aws.secretmanager.endpoint:NONE}")
    private String endpoint;
    @Value("${aws.access.key:NONE}")
    private String accessKey;
    @Value("${aws.secret.key:NONE}")
    private String secretKey;

    @Bean
    public SecretsManagerClient secretsManagerClient() {
        SecretsManagerClientBuilder clientBuilder = SecretsManagerClient.builder();

        if(!endpoint.equals("NONE")) {
            clientBuilder.endpointOverride(URI.create(endpoint))
                    .region(Region.of(region));
        } else {
            clientBuilder.region(Region.of(region));
        }

        if(!accessKey.equals("NONE") || !secretKey.equals("NONE")) {
            AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
            AwsCredentialsProvider awsCredentialsProvider = StaticCredentialsProvider.create(awsCredentials);

            clientBuilder.credentialsProvider(awsCredentialsProvider);
        }

        return clientBuilder.build();
    }
}
