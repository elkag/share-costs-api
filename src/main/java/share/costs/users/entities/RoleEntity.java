package share.costs.users.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="roles")
public class RoleEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", length = Constants.UUID_SIZE)
    private String id;

    @Column(nullable = false)
    private String role;

    public RoleEntity setRole(String role) {
        this.role = role;
        return this;
    }
}
