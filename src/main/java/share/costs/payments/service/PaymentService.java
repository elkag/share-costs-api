package share.costs.payments.service;

import share.costs.groups.model.GroupModel;
import share.costs.payments.rest.PaymentRequest;
import share.costs.users.model.UserModel;

import java.math.BigDecimal;

public interface PaymentService {
    void makePayment(PaymentRequest paymentRequest, String username);
}
