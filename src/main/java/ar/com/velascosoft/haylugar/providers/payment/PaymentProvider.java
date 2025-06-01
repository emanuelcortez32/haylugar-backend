package ar.com.velascosoft.haylugar.providers.payment;

import ar.com.velascosoft.haylugar.pojo.Customer;
import ar.com.velascosoft.haylugar.pojo.Payment;

public interface PaymentProvider {
    Payment createPayment(String customerId, String customerEmail, double amount, String description);
    Customer searchCustomer(String customerEmail);
    Customer createCustomer(String name, String lastName, String email, String dniNumber);
    Payment updatePayment(Payment payment);
    Payment getPayment(String paymentId);
    Payment getPaymentByOrder(Long orderId);
}
