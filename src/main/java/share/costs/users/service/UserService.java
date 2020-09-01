package share.costs.users.service;

import share.costs.users.model.UserModel;
import share.costs.users.oauth2.userInfo.OAuth2UserInfo;

public interface UserService {

  UserModel getOrCreateUser(String email);

  UserModel getOrCreateUser(OAuth2UserInfo oAuth2UserInfo);

  UserModel registerUser(UserModel model);

  UserModel getUserInfoByEmail(String email);


}
