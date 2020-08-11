package share.costs.groups.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;
import static share.costs.groups.constants.GroupConstants.GROUP_STATUS_ACTIVE;
import share.costs.users.entities.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "bunches")
public class Group {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", length = Constants.UUID_SIZE)
    private String id;

    @Column(name="name", nullable = false)
    @NotNull(message = "Name of the group can not be null")
    private String name;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();

    @Column(name = "status", nullable = false, columnDefinition = "varchar(36) default 'new'")
    private String status = GROUP_STATUS_ACTIVE;

    @Column(name = "group_balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "varchar(255)")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bunches_users"))
    private User owner;

    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<GroupUserBalance> groupUserBalances;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_pending_users",
            joinColumns = {@JoinColumn(name = "group_pending_id", nullable = true,
                    foreignKey = @ForeignKey(name = "fk_group_pending_users_bunches"))},
            inverseJoinColumns = {@JoinColumn(name = "group_pending_user_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_group_pending_users_users"))})
    private List<User> pendingUsers = new ArrayList<User>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_users",
            joinColumns = {@JoinColumn(name = "bunch_id", nullable = true,
                    foreignKey = @ForeignKey(name = "fk_bunch_users_bunches"))},
            inverseJoinColumns = {@JoinColumn(name = "bunch_user_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_bunch_users_users"))})
    private List<User> users = new ArrayList<User>();


}
