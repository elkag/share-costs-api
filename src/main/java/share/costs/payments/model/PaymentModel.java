package share.costs.payments.model;

import lombok.Data;
import share.costs.groups.model.GroupModel;
import share.costs.payments.entities.PaymentType;
import share.costs.users.model.UserModel;

import java.util.Date;

@Data
public class PaymentModel {
    private Long id;
    private PaymentType method;
    private String description;
    private Integer amount;
    private Date date;
    private GroupModel group;
    private UserModel user;
}
