package share.costs.users.model;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
public class GroupUserModel {
    private String id;

    private String username;

    @ToString.Exclude
    private String password;

    private String firstName;

    private String lastName;

    @ToString.Exclude
    private String email;

    private BigDecimal balance = BigDecimal.ZERO;

    private BigDecimal costs = BigDecimal.ZERO;

    private BigDecimal spending = BigDecimal.ZERO;
}
