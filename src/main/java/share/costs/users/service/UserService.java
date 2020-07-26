package share.costs.users.service;

import share.costs.users.model.UserModel;
import share.costs.users.rest.LoginResponse;
import share.costs.users.rest.UsernameCheckResponse;

public interface UserService {

  UserModel registerUser(UserModel model);

  LoginResponse loginUser(String username, String password);

  UsernameCheckResponse checkUsername(String username);
}
