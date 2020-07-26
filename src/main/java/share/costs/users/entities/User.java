package share.costs.users.entities;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import share.costs.constants.Constants;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "id", length = Constants.UUID_SIZE)
  private String id;

  @Column(unique = true, nullable = false)
  @Size(min = 3, max = 30, message
          = "Username must be between 3 and 30 characters")
  @NotNull(message = "Username cannot be null")
  private String username;

  @Column(nullable = false)
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

  @Column(nullable = false)
  @NotNull(message = "Name cannot be null")
  @Email(message = "Invalid email")
  private String email;

}
