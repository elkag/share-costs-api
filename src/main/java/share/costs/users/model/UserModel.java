package share.costs.users.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import share.costs.users.entities.AuthorityEntity;

import java.util.List;

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

  public String getId() {
    return id;
  }

  public UserModel setId(String id) {
    this.id = id;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public UserModel setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getFirstName() {
    return firstName;
  }

  public UserModel setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public String getLastName() {
    return lastName;
  }

  public UserModel setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public UserModel setEmail(String email) {
    this.email = email;
    return this;
  }

  public List<AuthorityEntity> getRoles() {
    return roles;
  }

  public UserModel setRoles(List<AuthorityEntity> roles) {
    this.roles = roles;
    return this;
  }
}