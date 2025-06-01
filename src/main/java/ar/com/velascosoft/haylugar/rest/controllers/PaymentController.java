package ar.com.velascosoft.haylugar.rest.controllers;

import ar.com.velascosoft.haylugar.rest.requests.ProcessPaymentRequest;
import ar.com.velascosoft.haylugar.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/payment/process")
    public void processPayment(@RequestBody ProcessPaymentRequest request) {
        request.validate();
        paymentService.processPayment(Long.valueOf(request.getOrderId()));
    }
}
