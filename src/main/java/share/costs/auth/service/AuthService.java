package share.costs.auth.service;

import share.costs.auth.model.RegistrationRequest;
import share.costs.users.entities.UserEntity;
import share.costs.users.model.UserModel;

public interface AuthService {

    UserModel getUserByEmail(String email);

    UserModel registerUser(RegistrationRequest model);

    UserModel validateAndLoginFacebookUser(String accessToken);
}
