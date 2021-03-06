package share.costs.users.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;

import javax.persistence.*;

@Data
@Entity
@Table(name="pending_users")
public class PendingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pending_users_user"))
    private UserEntity user;
}
