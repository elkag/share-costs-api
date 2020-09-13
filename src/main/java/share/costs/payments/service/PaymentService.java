package share.costs.payments.service;

import share.costs.payments.rest.PaymentRequest;

public interface PaymentService {
    void makePayment(PaymentRequest paymentRequest, String username);
}
