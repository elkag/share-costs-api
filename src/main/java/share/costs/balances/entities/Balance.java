package share.costs.balances.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;
import share.costs.groups.entities.GroupUserBalance;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "balances")
public class Balance {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", length = Constants.UUID_SIZE)
    private String id;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate = new Date();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<GroupUserBalance> groupUserBalance;
}
