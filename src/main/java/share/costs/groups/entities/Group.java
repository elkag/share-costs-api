package share.costs.groups.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;
import share.costs.users.entities.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bunches_users"))
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "bunches_users",
            joinColumns = {@JoinColumn(name = "bunch_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_bunches_users_groups"))},
            inverseJoinColumns = {@JoinColumn(name = "bunches_users_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_bunches_users_users"))})
    private List<User> users;


}
