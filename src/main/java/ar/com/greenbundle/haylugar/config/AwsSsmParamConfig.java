package ar.com.greenbundle.haylugar.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSsmParamConfig {
    @Value("${aws.region}")
    private String region;
    @Value("${aws.ssm.endpoint:NONE}")
    private String endpoint;
    @Value("${aws.access.key:NONE}")
    private String accessKey;
    @Value("${aws.secret.key:NONE}")
    private String secretKey;

    @Bean
    public AWSSimpleSystemsManagement awsSimpleSystemsManagement() {
        AWSSimpleSystemsManagementClientBuilder clientBuilder = AWSSimpleSystemsManagementClientBuilder.standard();

        if(!endpoint.equals("NONE")) {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
            clientBuilder.withEndpointConfiguration(endpointConfiguration);
        } else {
            clientBuilder.withRegion(region);
        }

        if(accessKey.equals("NONE") || secretKey.equals("NONE")) {
            DefaultAWSCredentialsProviderChain awsCredentialsProviderChain = new DefaultAWSCredentialsProviderChain();

            clientBuilder.withCredentials(awsCredentialsProviderChain);

        } else {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

            clientBuilder.withCredentials(credentialsProvider);
        }

        return clientBuilder.build();
    }
}
