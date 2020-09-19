package share.costs.users.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import share.costs.groups.entities.GroupUserBalance;
import share.costs.constants.Constants;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NotNull(message = "Email cannot be null")
  @Email(message = "Invalid email")
  private String email;

  @Column
  @ToString.Exclude
  private String password;

  @Size(min = 1, max = 30, message
          = "First name must be between 3 and 30 characters")
  @NotNull(message = "Name cannot be null")
  @Column(nullable = false)
  private String firstName;

  @Size(min = 1, max = 30, message
          = "Last name must be between 3 and 30 characters")
  @NotNull(message = "Last name cannot be null")
  @Column(nullable = false)
  private String lastName;

  @Column
  private BigDecimal balance = BigDecimal.ZERO;

  @Column
  private String image = "";

  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private List<GroupUserBalance> groupUserBalances;

  @OneToMany(
          fetch = FetchType.EAGER,
          cascade = CascadeType.ALL,
          orphanRemoval = true)
  @JoinColumn(name="user_id")
  private List<AuthorityEntity> roles;

  public UserEntity setRoles(List<AuthorityEntity> roles) {
    this.roles = roles;
    return this;
  }
}
