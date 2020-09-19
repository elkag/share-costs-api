package share.costs.payments.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;
import share.costs.groups.entities.Group;
import share.costs.users.entities.UserEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date(); // Current date

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private PaymentType type;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_users_id"))
    private UserEntity user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bunch_id", foreignKey = @ForeignKey(name = "fk_bunches_id"))
    private Group group;

    @OneToMany(mappedBy="payment")
    private List<UserInPayment> usersInPayment;

}