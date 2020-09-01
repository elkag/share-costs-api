package share.costs.users.model;

import lombok.Data;
import share.costs.users.entities.UserEntity;

@Data
public class PendingUserModel {
    private String id;

    private UserEntity user;
}
