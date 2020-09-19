package share.costs.payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import share.costs.payments.entities.Payment;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInPaymentModel {

    private Long id;

    private Integer weight = 1;

    // Amount in cents
    private Integer amount = 0;

    private Long userId;

    private Payment payment;
}
