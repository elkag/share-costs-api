package share.costs.groups.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;
import static share.costs.groups.constants.GroupConstants.GROUP_STATUS_ACTIVE;

import share.costs.users.entities.PendingUser;
import share.costs.users.entities.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "bunches")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    @NotNull(message = "Name of the group can not be null")
    private String name;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();

    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'new'")
    private String status = GROUP_STATUS_ACTIVE;

    @Column(name = "group_balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "varchar(255)")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bunches_users"))
    private UserEntity owner;

    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<GroupUserBalance> groupUserBalances;

    @ManyToMany
    @JoinTable(name = "group_pending_users",
            joinColumns = {@JoinColumn(name = "group_id",
                    foreignKey = @ForeignKey(name = "fk_group_pending_user_group"))},
            inverseJoinColumns = {@JoinColumn(name = "user_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_group_pending_user_user"))})
    private List<PendingUser> pendingUsers = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "group_users",
            joinColumns = {@JoinColumn(name = "group_id",
                    foreignKey = @ForeignKey(name = "fk_group_user_group"))},
            inverseJoinColumns = {@JoinColumn(name = "user_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_group_user_user"))})
    private List<UserEntity> users = new ArrayList<>();


}
