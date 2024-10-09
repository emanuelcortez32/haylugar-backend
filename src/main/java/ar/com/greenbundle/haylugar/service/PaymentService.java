package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dto.CreatePaymentData;
import ar.com.greenbundle.haylugar.dto.PaymentCard;
import ar.com.greenbundle.haylugar.dto.PaymentProfile;
import ar.com.greenbundle.haylugar.dto.PaymentTransactionDetail;
import ar.com.greenbundle.haylugar.dto.constants.PaymentStatus;
import ar.com.greenbundle.haylugar.entities.BookingItem;
import ar.com.greenbundle.haylugar.entities.PaymentItem;
import ar.com.greenbundle.haylugar.entities.UserItem;
import ar.com.greenbundle.haylugar.exceptions.PaymentProcessException;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import ar.com.greenbundle.haylugar.repositories.PaymentRepository;
import ar.com.greenbundle.haylugar.repositories.SpotRepository;
import ar.com.greenbundle.haylugar.repositories.UserRepository;
import ar.com.greenbundle.haylugar.rest.clients.ThirdPartyClientResponse;
import ar.com.greenbundle.haylugar.rest.clients.mercadopago.MercadoPagoClient;
import ar.com.greenbundle.haylugar.util.StringUtils;
import com.mercadopago.resources.CardToken;
import com.mercadopago.resources.customer.Customer;
import com.mercadopago.resources.customer.CustomerCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static ar.com.greenbundle.haylugar.dto.constants.PaymentProvider.MERCADO_PAGO;

@Service
public class PaymentService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MercadoPagoClient mercadoPagoClient;

    public Mono<PaymentItem> createPayment(CreatePaymentData paymentData) {

        PaymentItem payment = PaymentItem.builder()
                .method(paymentData.getPaymentMethod())
                .currency(paymentData.getCurrency())
                .build();

        return paymentRepository.insert(payment);
    }


    public Mono<PaymentCard> addCardForUser(String userId, String cardToken, String cardSecurityCode) {
        return userRepository.findById(userId)
                .flatMap(user -> {

                    ThirdPartyClientResponse searchCustomerResponse = mercadoPagoClient.searchCustomerByEmail(user.getEmail());

                    if(!searchCustomerResponse.isSuccess())
                        return Mono.error(new PaymentProcessException((String) searchCustomerResponse.getData()));

                    Customer customer = (Customer) searchCustomerResponse.getData();

                    if(user.getProfile().getPaymentProfile() == null) {
                        PaymentProfile paymentProfile = PaymentProfile.builder()
                                .customerId(customer.getId())
                                .build();

                        user.getProfile().setPaymentProfile(paymentProfile);
                    }

                    ThirdPartyClientResponse customerCardResponse = mercadoPagoClient.addCardCustomer(customer.getId(), cardToken);

                    if(!customerCardResponse.isSuccess())
                        return Mono.error(new PaymentProcessException((String) customerCardResponse.getData()));

                    CustomerCard customerCard = (CustomerCard) customerCardResponse.getData();

                    PaymentCard paymentCard = PaymentCard.builder().id(customerCard.getId()).securityCode(cardSecurityCode).build();

                    if(user.getProfile().getPaymentProfile() == null) {
                        PaymentProfile paymentProfile = PaymentProfile.builder()
                                .paymentCards(List.of(paymentCard))
                                .build();

                        user.getProfile().setPaymentProfile(paymentProfile);

                    } else {
                        if(user.getProfile().getPaymentProfile().getPaymentCards() == null) {
                            user.getProfile().getPaymentProfile().setPaymentCards(List.of(paymentCard));
                        } else {
                            user.getProfile().getPaymentProfile().getPaymentCards().add(paymentCard);
                        }
                    }

                    return userRepository.save(user)
                            .then(Mono.just(paymentCard));
                });
    }

    public Mono<PaymentItem> processPaymentForBooking(BookingItem booking, double amount) {

        return userRepository.findById(booking.getClientUserId())
                .zipWhen(user -> paymentRepository.findById(booking.getPaymentId()).switchIfEmpty(Mono.error(new ResourceNotFoundException("Payment not found"))))
                .flatMap(tuple -> {
                    UserItem user = tuple.getT1();
                    PaymentItem bookingPayment = tuple.getT2();

                    if(user.getProfile().getPaymentProfile() == null || StringUtils.isNullOrEmpty(user.getProfile().getPaymentProfile().getCustomerId()))
                        return Mono.error(new PaymentProcessException("User not have payment profile"));

                    if(user.getProfile().getPaymentProfile().getPaymentCards().isEmpty())
                        return Mono.error(new PaymentProcessException("User not have saved cards"));

                    String customerId = user.getProfile().getPaymentProfile().getCustomerId();

                    PaymentCard paymentCard = user.getProfile().getPaymentProfile().getPaymentCards().get(0);

                    ThirdPartyClientResponse cardTokenResponse = mercadoPagoClient.createCardToken(customerId, paymentCard.getId(), paymentCard.getSecurityCode());

                    if(!cardTokenResponse.isSuccess())
                        return Mono.error(new PaymentProcessException((String) cardTokenResponse.getData()));

                    CardToken cardToken = (CardToken) cardTokenResponse.getData();

                    ThirdPartyClientResponse paymentResponse = mercadoPagoClient.createPayment(customerId, user.getEmail(), cardToken.getId(),
                            amount, "Pago por uso de garage");

                    if(!paymentResponse.isSuccess())
                        return Mono.error(new PaymentProcessException((String) paymentResponse.getData()));

                    com.mercadopago.resources.payment.Payment payment = (com.mercadopago.resources.payment.Payment) paymentResponse.getData();

                    BigDecimal totalPaidAmount = payment.getTransactionDetails().getTotalPaidAmount();
                    BigDecimal netReceivedAmount = payment.getTransactionDetails().getNetReceivedAmount();

                    BigDecimal netAmountUser = calculateCommissionPercentage(totalPaidAmount, new BigDecimal("85"));
                    BigDecimal amountProvider = totalPaidAmount.subtract(netReceivedAmount);
                    BigDecimal amountPlatform = netReceivedAmount.subtract(netAmountUser);

                    bookingPayment.setProvider(MERCADO_PAGO);
                    bookingPayment.setTotalPrice(totalPaidAmount.doubleValue());
                    bookingPayment.setProviderAmount(amountProvider.doubleValue());
                    bookingPayment.setPlatformAmount(amountPlatform.doubleValue());
                    bookingPayment.setUserNetAmount(netAmountUser.doubleValue());

                    bookingPayment.getTransactionDetails().add(PaymentTransactionDetail.builder()
                            .status(PaymentStatus.COMPLETED)
                            .build());

                    return paymentRepository.save(bookingPayment);
                });
    }

    private BigDecimal calculateCommissionPercentage(BigDecimal value, BigDecimal percentage) {
        return value.multiply(percentage).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }
}
