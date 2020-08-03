package share.costs.users.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

  private String id;

  private String username;

  @ToString.Exclude
  private String password;

  private String firstName;

  private String lastName;

  @ToString.Exclude
  private String email;
}