package share.costs.payments.model;

import lombok.Data;
import share.costs.groups.entities.Group;
import share.costs.groups.model.GroupModel;
import share.costs.users.entities.User;
import share.costs.users.model.UserModel;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentModel {
    private String id;
    private String description;
    private BigDecimal amount;
    private Date date;
    private GroupModel group;
    private UserModel user;
}
