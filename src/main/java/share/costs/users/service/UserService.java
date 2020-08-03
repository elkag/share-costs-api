package share.costs.users.service;

import share.costs.groups.model.GroupModel;
import share.costs.users.entities.User;
import share.costs.users.model.UserModel;
import share.costs.users.rest.LoginResponse;
import share.costs.users.rest.UsernameCheckResponse;

import java.util.List;

public interface UserService {

  UserModel registerUser(UserModel model);

  LoginResponse loginUser(String username, String password);

  UsernameCheckResponse checkUsername(String username);

  void joinGroup(String groupId);

  public List<UserModel> findUsers(String searchValue);

}
