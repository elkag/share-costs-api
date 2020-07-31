package share.costs.payments.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;
import share.costs.groups.entities.Group;
import share.costs.users.entities.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name="payments")
public class Payment {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", length = Constants.UUID_SIZE)
    private String id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date(); // Current date

    @Column(name = "description")
    private String description;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_users_id"))
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bunch_id", foreignKey = @ForeignKey(name = "fk_bunches_id"))
    private Group group;
}
