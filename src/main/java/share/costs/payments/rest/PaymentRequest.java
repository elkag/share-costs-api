package share.costs.payments.rest;

import lombok.Data;
import share.costs.payments.entities.PaymentType;
import share.costs.payments.entities.UserInPayment;
import share.costs.payments.model.UserInPaymentModel;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaymentRequest {

    private String groupId;

    String description;

    private PaymentType type;

    private Integer total;

    List<UserInPaymentModel> users;

}
