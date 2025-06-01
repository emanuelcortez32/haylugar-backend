package ar.com.velascosoft.haylugar.config;

import ar.com.velascosoft.haylugar.entities.converters.JsonToPaymentTransactionDetailsConverter;
import ar.com.velascosoft.haylugar.entities.converters.JsonToSpotAddressConverter;
import ar.com.velascosoft.haylugar.entities.converters.PaymentTransactionDetailsToJsonConverter;
import ar.com.velascosoft.haylugar.entities.converters.SpotAddressToJsonConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class DatabaseConfig extends AbstractR2dbcConfiguration {
    @Value("${r2dbc.host}")
    private String host;

    @Value("${r2dbc.port}")
    private Integer port;

    @Value("${r2dbc.password}")
    private String password;

    @Value("${r2dbc.db}")
    private String database;

    @Value("${r2dbc.username}")
    private String username;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .username(username)
                .password(password)
                .database(database)
                .build());
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager() {
        return new R2dbcTransactionManager(connectionFactory());
    }

    @Bean
    public TransactionalOperator transactionalOperator() {
        return TransactionalOperator.create(reactiveTransactionManager());
    }

    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> customConverters = new ArrayList<>();

        customConverters.add(new SpotAddressToJsonConverter(objectMapper));
        customConverters.add(new PaymentTransactionDetailsToJsonConverter(objectMapper));

        customConverters.add(new JsonToSpotAddressConverter(objectMapper));
        customConverters.add(new JsonToPaymentTransactionDetailsConverter(objectMapper));

        return new R2dbcCustomConversions(getStoreConversions(), customConverters);
    }
}
