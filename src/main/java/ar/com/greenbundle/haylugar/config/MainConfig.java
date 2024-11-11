package ar.com.greenbundle.haylugar.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;

@Configuration
public class MainConfig {
    @Value("${app.representation.timezone}")
    private String appRepresentationTimeZone;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public ZoneId representationTimeZone() {
        return ZoneId.of(appRepresentationTimeZone);
    }

    @Bean
    public BeanUtilsBean beanUtils() {
        return new BeanUtilsBean() {
            @Override
            public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
                try {
                    if (value != null && getProperty(bean, name) == null) {
                        super.copyProperty(bean, name, value);
                    }
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
