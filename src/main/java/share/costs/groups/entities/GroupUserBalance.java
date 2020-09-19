package share.costs.groups.entities;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import share.costs.balances.entities.Balance;
import share.costs.constants.Constants;
import share.costs.users.entities.UserEntity;

import javax.persistence.*;

@Data
@Entity
@Table(name = "group_user_balances")
public class GroupUserBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "balance_id")
    private Balance balance;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @ToString.Exclude
    private Group group;
}
