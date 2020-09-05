package share.costs.users.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import share.costs.users.entities.AuthorityEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

  private String id;

  @ToString.Exclude
  private String password;

  private String firstName;

  private String lastName;

  @ToString.Exclude
  private String email;

  private List<AuthorityEntity> roles;
}