package share.costs.users.service.converters;

import share.costs.users.entities.UserEntity;
import share.costs.users.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

  public UserModel convertToModel(final UserEntity user) {
    if (user == null) {
      return null;
    }

    final UserModel model = new UserModel();
    model.setId(user.getId());
    model.setFirstName(user.getFirstName());
    model.setLastName(user.getLastName());
    model.setEmail(user.getEmail());
    model.setRoles(user.getRoles());

    return model;
  }

  public UserEntity convertToEntity(final UserModel model){
    if(model == null){
      return null;
    }

    final UserEntity user = new UserEntity();
    user.setId(model.getId());
    user.setPassword(model.getPassword());
    user.setFirstName(model.getFirstName());
    user.setLastName(model.getLastName());
    user.setEmail(model.getEmail());
    user.setRoles(model.getRoles());

    return user;
  }

}
