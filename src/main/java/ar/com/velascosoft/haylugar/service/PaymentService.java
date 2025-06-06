package ar.com.velascosoft.haylugar.service;

import ar.com.velascosoft.haylugar.dao.PaymentDao;
import ar.com.velascosoft.haylugar.dto.BookingDto;
import ar.com.velascosoft.haylugar.dto.PaymentDto;
import ar.com.velascosoft.haylugar.dto.UserDto;
import ar.com.velascosoft.haylugar.dto.UserPaymentProfileDto;
import ar.com.velascosoft.haylugar.exceptions.PaymentProcessException;
import ar.com.velascosoft.haylugar.exceptions.ResourceNotFoundException;
import ar.com.velascosoft.haylugar.pojo.Customer;
import ar.com.velascosoft.haylugar.pojo.Payment;
import ar.com.velascosoft.haylugar.pojo.PaymentTransactionDetail;
import ar.com.velascosoft.haylugar.pojo.constants.PaymentMethod;
import ar.com.velascosoft.haylugar.providers.payment.PaymentProvider;
import ar.com.velascosoft.haylugar.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static ar.com.velascosoft.haylugar.pojo.constants.PaymentStatus.PENDING;

@Slf4j
@Service
public class PaymentService {
    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private PaymentProvider paymentProvider;

    public Mono<String> createPaymentProfileForUser(UserDto userDto) {

        return paymentDao.getPaymentProfileByUser(userDto.getId())
                .map(paymentProfile -> {
                    log.info("User already have payment profile");
                    return paymentProfile.getId();
                })
                .switchIfEmpty(Mono.defer(() -> createNewPaymentProfile(userDto)));
    }

    public Mono<PaymentDto> getPayment(String paymentId) {
        return paymentDao.getPayment(paymentId);
    }

    public Mono<PaymentDto> savePayment(PaymentDto paymentDto) {
        return paymentDao.savePayment(paymentDto);
    }

    public Mono<Payment> createPaymentIntentForUser(UserDto user, double amount, String description) {
        Payment payment = paymentProvider.createPayment(user.getProfile().getPaymentProfile().getExternalReferenceId(),
                user.getEmail(), amount, description);

        PaymentDto basePayment = PaymentDto.builder()
                .externalReferenceId(payment.getId())
                .method(PaymentMethod.NOT_DEFINED)
                .provider(payment.getProvider())
                .currency(payment.getCurrency())
                .lastStatus(payment.getStatus())
                .build();

        if(basePayment.getTransactionDetails() == null) {
            basePayment.setTransactionDetails(new ArrayList<>());
        }

        basePayment.getTransactionDetails().add(createTransactionDetail(payment));

        return paymentDao.savePayment(basePayment)
                .then(Mono.just(payment));
    }

    public Mono<PaymentDto> processPayment(Long orderId) {
        Payment payment = paymentProvider.getPaymentByOrder(orderId);

        return Mono.empty();
    }

    public Mono<PaymentDto> createPaymentSkeleton(UserDto client, PaymentDto basePayment) {
        Payment payment = paymentProvider.createPayment(client.getProfile().getPaymentProfile().getExternalReferenceId(),
                client.getEmail(), 1, "");

        updateBookingPayment(basePayment, payment);
        return Mono.just(basePayment);
    }

    public Mono<PaymentDto> processPaymentForBooking(BookingDto bookingDto, double amount) {
        if (isUserPaymentProfileInvalid(bookingDto))
            return Mono.error(new PaymentProcessException("User not have payment profile"));

        if (isBookingPaymentInvalid(bookingDto))
            return Mono.error(new PaymentProcessException("Booking not have initial payment created"));

        return paymentDao.getPayment(bookingDto.getPayment().getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Payment not found")))
                .flatMap(bookingPayment -> {

                    if(!bookingPayment.getLastStatus().equals(PENDING))
                        return Mono.error(new PaymentProcessException("Payment is not pending"));

                    return processFreshPayment(bookingDto, bookingPayment, amount);
                });
    }

    private Mono<String> createNewPaymentProfile(UserDto userDto) {
        Customer customer = paymentProvider.searchCustomer(userDto.getEmail());

        if (customer == null) {
            customer = paymentProvider.createCustomer(
                    userDto.getProfile().getName(),
                    userDto.getProfile().getLastName(),
                    userDto.getEmail(),
                    userDto.getProfile().getDni()
            );
        }

        UserPaymentProfileDto paymentProfileDto = UserPaymentProfileDto.builder()
                .user(userDto)
                .externalReferenceId(customer.getId())
                .cards(List.of())
                .build();

        return paymentDao.savePaymentProfile(paymentProfileDto)
                .map(UserPaymentProfileDto::getId);
    }

    private boolean isUserPaymentProfileInvalid(BookingDto bookingDto) {
        return bookingDto.getClient().getProfile().getPaymentProfile() == null ||
                StringUtils.isNullOrEmpty(bookingDto.getClient().getProfile().getPaymentProfile().getExternalReferenceId());
    }

    private boolean isBookingPaymentInvalid(BookingDto bookingDto) {
        return bookingDto.getPayment() == null || StringUtils.isNullOrEmpty(bookingDto.getPayment().getId());
    }

    private void updateBookingPayment(PaymentDto bookingPayment, Payment payment) {
        bookingPayment.setExternalReferenceId(payment.getId());
        bookingPayment.setProvider(payment.getProvider());
        bookingPayment.setTotalPrice(payment.getTotalPrice());
        bookingPayment.setProviderAmount(payment.getProviderAmount());
        bookingPayment.setPlatformAmount(payment.getPlatformAmount());
        bookingPayment.setUserNetAmount(payment.getUserNetAmount());
        bookingPayment.setLastStatus(payment.getStatus());
        bookingPayment.setCurrency(payment.getCurrency());

        if(bookingPayment.getTransactionDetails() == null) {
            bookingPayment.setTransactionDetails(new ArrayList<>());
        }

        bookingPayment.getTransactionDetails().add(createTransactionDetail(payment));
    }

    private PaymentTransactionDetail createTransactionDetail(Payment payment) {
        return PaymentTransactionDetail.builder()
                .date(payment.getDateCreated())
                .status(payment.getStatus())
                .statusDetail(payment.getStatusDetail())
                .build();
    }

    private Mono<PaymentDto> processFreshPayment(BookingDto bookingDto, PaymentDto bookingPayment, double amount) {
        String customerId = bookingDto.getClient().getProfile().getPaymentProfile().getExternalReferenceId();
        String customerEmail = bookingDto.getClient().getEmail();

        Payment payment = paymentProvider.createPayment(customerId, customerEmail, amount, "");

        updateBookingPayment(bookingPayment, payment);

        return paymentDao.savePayment(bookingPayment);
    }
}
