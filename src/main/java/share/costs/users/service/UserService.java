package share.costs.users.service;

import share.costs.users.entities.UserEntity;
import share.costs.users.model.UserModel;

public interface UserService {

  UserModel getOrCreateUser(String email);

  UserEntity getUser(final String email);

  UserModel registerUser(UserModel model);

  UserModel validateAndLogFacebookUser(String accessToken);
}
