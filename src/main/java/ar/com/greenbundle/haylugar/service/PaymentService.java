package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dao.PaymentDao;
import ar.com.greenbundle.haylugar.dto.BookingDto;
import ar.com.greenbundle.haylugar.dto.EntityDto;
import ar.com.greenbundle.haylugar.dto.PaymentDto;
import ar.com.greenbundle.haylugar.dto.UserDto;
import ar.com.greenbundle.haylugar.dto.UserPaymentProfileDto;
import ar.com.greenbundle.haylugar.exceptions.PaymentProcessException;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import ar.com.greenbundle.haylugar.pojo.Customer;
import ar.com.greenbundle.haylugar.pojo.Payment;
import ar.com.greenbundle.haylugar.pojo.PaymentTransactionDetail;
import ar.com.greenbundle.haylugar.providers.payment.PaymentProvider;
import ar.com.greenbundle.haylugar.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus.PENDING;

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
                    return paymentProfile.getUserId();
                })
                .switchIfEmpty(Mono.defer(() -> createNewPaymentProfile(userDto)));
    }

    public Mono<PaymentDto> getPayment(String paymentId) {
        return paymentDao.getPayment(paymentId);
    }

    public Mono<String> savePayment(PaymentDto paymentDto) {
        return paymentDao.savePayment(paymentDto)
                .map(EntityDto::getId);
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
                .userId(userDto.getId())
                .customerId(customer.getId())
                .cards(customer.getCards())
                .build();

        return paymentDao.savePaymentProfile(paymentProfileDto)
                .map(UserPaymentProfileDto::getId);
    }

    private boolean isUserPaymentProfileInvalid(BookingDto bookingDto) {
        return bookingDto.getClient().getProfile().getPaymentProfile() == null ||
                StringUtils.isNullOrEmpty(bookingDto.getClient().getProfile().getPaymentProfile().getCustomerId());
    }

    private boolean isBookingPaymentInvalid(BookingDto bookingDto) {
        return bookingDto.getPayment() == null || StringUtils.isNullOrEmpty(bookingDto.getPayment().getId());
    }

    private void updateBookingPayment(PaymentDto bookingPayment, Payment payment) {
        bookingPayment.setReferenceId(payment.getId());
        bookingPayment.setMethod(payment.getMethod());
        bookingPayment.setProvider(payment.getProvider());
        bookingPayment.setTotalPrice(payment.getTotalPrice());
        bookingPayment.setProviderAmount(payment.getProviderAmount());
        bookingPayment.setPlatformAmount(payment.getPlatformAmount());
        bookingPayment.setUserNetAmount(payment.getUserNetAmount());
        bookingPayment.setLastStatus(payment.getStatus());

        bookingPayment.getTransactionDetails()
                .add(createTransactionDetail(payment));
    }

    private PaymentTransactionDetail createTransactionDetail(Payment payment) {
        return PaymentTransactionDetail.builder()
                .date(payment.getDateCreated())
                .status(payment.getStatus())
                .statusDetail(payment.getStatusDetail())
                .build();
    }

    private Mono<PaymentDto> processFreshPayment(BookingDto bookingDto, PaymentDto bookingPayment, double amount) {
        String cardToken = "38db57561beef9ed05fb7b51ba9cd69d";
        String customerId = bookingDto.getClient().getProfile().getPaymentProfile().getCustomerId();
        String customerEmail = bookingDto.getClient().getEmail();

        Payment payment = paymentProvider.createPayment(customerId, customerEmail, cardToken, amount);

        updateBookingPayment(bookingPayment, payment);

        return paymentDao.savePayment(bookingPayment);
    }
}
