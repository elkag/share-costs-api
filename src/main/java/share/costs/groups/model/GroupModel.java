package share.costs.groups.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import share.costs.users.entities.User;
import share.costs.users.model.UserModel;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupModel {
    String id;
    String name;
    UserModel owner;
    List<UserModel> users;
}
