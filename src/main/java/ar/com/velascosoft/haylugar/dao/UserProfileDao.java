package ar.com.velascosoft.haylugar.dao;

import ar.com.velascosoft.haylugar.dto.UserPaymentProfileDto;
import ar.com.velascosoft.haylugar.dto.UserProfileDto;
import ar.com.velascosoft.haylugar.repositories.UserPaymentProfileRepository;
import ar.com.velascosoft.haylugar.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserProfileDao {
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserPaymentProfileRepository userPaymentProfileRepository;

    public Mono<UserProfileDto> getProfileByUser(String userId) {
        return userProfileRepository.findByUserId(userId)
                .map(profile -> new UserProfileDto().dtoFromEntity(profile))
                .flatMap(profile -> userPaymentProfileRepository.findByUserId(userId)
                        .doOnNext(paymentProfile ->
                                profile.setPaymentProfile(new UserPaymentProfileDto().dtoFromEntity(paymentProfile)))
                        .then(Mono.just(profile)));
    }

    public Mono<UserProfileDto> saveProfile(UserProfileDto profileDto) {
        return userProfileRepository.save(new UserProfileDto().dtoToEntity(profileDto))
                .map(profile -> new UserProfileDto().dtoFromEntity(profile));
    }
}
