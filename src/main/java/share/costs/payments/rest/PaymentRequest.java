package share.costs.payments.rest;

import lombok.Data;
import share.costs.users.model.UserModel;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    private String description;

    private BigDecimal amount;

    private String groupId;
}
