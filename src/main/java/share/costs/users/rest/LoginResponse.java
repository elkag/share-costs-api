package share.costs.users.rest;

import org.springframework.security.core.userdetails.User;
import share.costs.users.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

  private UserModel user;
  private String jwtToken;
  private Boolean error = false;
  private String message = "";

  public LoginResponse(UserModel user, String jwtToken) {
    this.user = user;
    this.jwtToken = jwtToken;
  }
}
