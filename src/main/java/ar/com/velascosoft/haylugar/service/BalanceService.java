package ar.com.velascosoft.haylugar.service;

import ar.com.velascosoft.haylugar.dao.UserBalanceDao;
import ar.com.velascosoft.haylugar.dto.UserBalanceDto;
import ar.com.velascosoft.haylugar.pojo.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BalanceService {
    @Autowired
    private UserBalanceDao userBalanceDao;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserService userService;

    public Mono<UserBalanceDto> getUserBalance(String userId) {
        return userBalanceDao.getBalanceByUser(userId);
    }

    public Mono<UserBalanceDto> saveUserBalance(UserBalanceDto userBalanceDto) {
        return userBalanceDao.saveBalance(userBalanceDto);
    }

    public Mono<Payment> intentAddMinutesToUser(String userId, int minutes) {
        return userService.findUser(userId)
                .flatMap(user -> userBalanceDao.getBalanceByUser(user.getId())
                        .flatMap(balance -> {
                            final int MIN_PRICE_PER_MINUTE = 15;
                            final String DESCRIPTION = "Compra Minutos HayLugar!";
                            final double TOTAL_PRICE = minutes * MIN_PRICE_PER_MINUTE;

                            return paymentService.createPaymentIntentForUser(user, TOTAL_PRICE, DESCRIPTION);
                        }));
    }
}
