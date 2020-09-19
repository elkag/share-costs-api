package share.costs.balances.model;

import lombok.Data;
import share.costs.groups.entities.Group;
import share.costs.users.entities.UserEntity;

import java.math.BigDecimal;

@Data
public class BalanceModel {
    private Long id;
    private BigDecimal balance;
    private BigDecimal spending;
    private BigDecimal costs;
    private Group group;
    private UserEntity user;
}
