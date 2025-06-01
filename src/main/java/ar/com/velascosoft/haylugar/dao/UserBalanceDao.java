package ar.com.velascosoft.haylugar.dao;

import ar.com.velascosoft.haylugar.dto.UserBalanceDto;
import ar.com.velascosoft.haylugar.exceptions.ResourceNotFoundException;
import ar.com.velascosoft.haylugar.repositories.UserBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserBalanceDao {
    @Autowired
    private UserBalanceRepository userBalanceRepository;

    public Mono<UserBalanceDto> getBalanceByUser(String userId) {
        return userBalanceRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Balance Not Found")))
                .map(balance -> new UserBalanceDto().dtoFromEntity(balance));
    }

    public Mono<UserBalanceDto> saveBalance(UserBalanceDto userBalanceDto) {
        return userBalanceRepository.save(new UserBalanceDto().dtoToEntity(userBalanceDto))
                .map(balance -> new UserBalanceDto().dtoFromEntity(balance));
    }
}
