package ar.com.greenbundle.haylugar;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.time.Instant;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableReactiveMongoAuditing
@Slf4j
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		log.info("backend-application initialized !!! at {}", Instant.now());
	}

	@PreDestroy
	public void cleanup() {
		log.info("backend-application is being terminated at {}", Instant.now());
	}
}
