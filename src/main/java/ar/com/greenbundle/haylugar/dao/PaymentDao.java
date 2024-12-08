package ar.com.greenbundle.haylugar.dao;

import ar.com.greenbundle.haylugar.dto.PaymentDto;
import ar.com.greenbundle.haylugar.dto.UserPaymentCardDto;
import ar.com.greenbundle.haylugar.dto.UserPaymentProfileDto;
import ar.com.greenbundle.haylugar.repositories.PaymentRepository;
import ar.com.greenbundle.haylugar.repositories.UserPaymentCardRepository;
import ar.com.greenbundle.haylugar.repositories.UserPaymentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PaymentDao {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserPaymentProfileRepository userPaymentProfileRepository;
    @Autowired
    private UserPaymentCardRepository userPaymentCardRepository;

    public Mono<UserPaymentProfileDto> getPaymentProfileByUser(String userId) {
        return userPaymentProfileRepository.findByUserId(userId)
                .map(paymentProfile -> new UserPaymentProfileDto().dtoFromEntity(paymentProfile))
                .flatMap(paymentProfile -> userDao.getUser(paymentProfile.getUser().getId())
                        .map(user -> {
                            paymentProfile.setUser(user);
                            return paymentProfile;
                        }))
                .flatMap(paymentProfile -> userPaymentCardRepository.findPaymentCardsByPaymentProfile(paymentProfile.getId())
                        .collectList()
                        .map(paymentCards -> {
                            List<UserPaymentCardDto> cards = paymentCards.stream()
                                    .map(card -> new UserPaymentCardDto().dtoFromEntity(card))
                                    .toList();

                            paymentProfile.setCards(cards);

                            return paymentProfile;
                        }));
    }

    public Mono<PaymentDto> getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(payment -> new PaymentDto().dtoFromEntity(payment));
    }

    public Mono<PaymentDto> getPaymentByExternalReference(String externalReferenceId) {
        return paymentRepository.findByExternalReferenceId(externalReferenceId)
                .map(payment -> new PaymentDto().dtoFromEntity(payment));
    }

    public Mono<UserPaymentProfileDto> savePaymentProfile(UserPaymentProfileDto userPaymentProfileDto) {
        return userPaymentProfileRepository.save(new UserPaymentProfileDto().dtoToEntity(userPaymentProfileDto))
                .map(paymentProfile -> new UserPaymentProfileDto().dtoFromEntity(paymentProfile))
                .flatMap(paymentProfile -> userDao.getUser(paymentProfile.getUser().getId())
                        .map(user -> {
                            paymentProfile.setUser(user);
                            return paymentProfile;
                        }));

    }

    public Mono<PaymentDto> savePayment(PaymentDto paymentDto) {
        return paymentRepository.save(new PaymentDto().dtoToEntity(paymentDto))
                .map(payment ->new PaymentDto().dtoFromEntity(payment));
    }
}
