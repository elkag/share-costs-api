package share.costs.users.model;

import lombok.Data;
import share.costs.users.entities.User;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
public class PendingUserModel {
    private String id;

    private User user;
}
