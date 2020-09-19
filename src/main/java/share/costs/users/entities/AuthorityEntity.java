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
public class AuthorityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    public AuthorityEntity setRole(String role) {
        this.role = role;
        return this;
    }
}
