package ar.com.greenbundle.haylugar.dao;

import ar.com.greenbundle.haylugar.dto.PaymentDto;
import ar.com.greenbundle.haylugar.dto.UserPaymentCardDto;
import ar.com.greenbundle.haylugar.dto.UserPaymentProfileDto;
import ar.com.greenbundle.haylugar.repositories.PaymentRepository;
import ar.com.greenbundle.haylugar.repositories.UserPaymentCardRepository;
import ar.com.greenbundle.haylugar.repositories.UserPaymentProfileRepository;
import ar.com.greenbundle.haylugar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PaymentDao {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserPaymentProfileRepository userPaymentProfileRepository;
    @Autowired
    private UserPaymentCardRepository userPaymentCardRepository;

    public Mono<UserPaymentProfileDto> savePaymentProfile(UserPaymentProfileDto userPaymentProfileDto) {
        return userPaymentProfileRepository.save(UserPaymentProfileDto.mapToEntity(userPaymentProfileDto))
                .map(paymentProfile -> UserPaymentProfileDto.builderFromEntity(paymentProfile).build());

    }

    public Mono<UserPaymentProfileDto> getPaymentProfileByUser(String userId) {
        return userPaymentProfileRepository.findByUserId(userId)
                .map(paymentProfile -> UserPaymentProfileDto.builderFromEntity(paymentProfile).build())
                .flatMap(paymentProfile -> userPaymentCardRepository.findPaymentCardsByPaymentProfile(paymentProfile.getId()).collectList()
                        .map(paymentCards -> {
                            List<UserPaymentCardDto> cards = paymentCards.stream().map(card -> UserPaymentCardDto.builderFromEntity(card)
                                    .build()).toList();

                            paymentProfile.setCards(cards);

                            return paymentProfile;
                        }));
    }

    public Mono<PaymentDto> getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(payment -> PaymentDto.builderFromEntity(payment).build());
    }

    public Mono<PaymentDto> savePayment(PaymentDto paymentDto) {
        return paymentRepository.save(PaymentDto.mapToEntity(paymentDto))
                .map(payment -> PaymentDto.builderFromEntity(payment).build());
    }
}
