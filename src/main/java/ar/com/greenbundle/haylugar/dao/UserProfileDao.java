package ar.com.greenbundle.haylugar.dao;

import ar.com.greenbundle.haylugar.dto.UserPaymentProfileDto;
import ar.com.greenbundle.haylugar.dto.UserProfileDto;
import ar.com.greenbundle.haylugar.repositories.UserPaymentProfileRepository;
import ar.com.greenbundle.haylugar.repositories.UserProfileRepository;
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
                .map(profile -> UserProfileDto.builderFromEntity(profile).build())
                .flatMap(profile -> userPaymentProfileRepository.findByUserId(userId)
                        .doOnNext(paymentProfile -> profile.setPaymentProfile(UserPaymentProfileDto.builderFromEntity(paymentProfile).build())).then(Mono.just(profile)));
    }

    public Mono<UserProfileDto> saveProfile(UserProfileDto profileDto) {
        return userProfileRepository.save(UserProfileDto.mapToEntity(profileDto))
                .map(profile -> UserProfileDto.builderFromEntity(profile).build());
    }
}
