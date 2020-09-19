package share.costs.users.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;

import javax.persistence.*;

@Data
@Entity
@Table(name="group_pending_users")
public class GroupPendingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_group_user_user"))
    private UserEntity user;
}
