package share.costs.users.service.converters;

import lombok.Data;
import org.springframework.stereotype.Component;
import share.costs.balances.entities.Balance;
import share.costs.users.entities.PendingUser;
import share.costs.users.model.GroupUserModel;
import share.costs.users.entities.User;

@Component
@Data
public class GroupUserConverter {

    public final GroupUserModel convertToModel(PendingUser user) {

        final GroupUserModel model = new GroupUserModel();
        model.setId(user.getUser().getId());
        model.setUsername(user.getUser().getUsername());
        model.setFirstName(user.getUser().getFirstName());
        model.setLastName(user.getUser().getLastName());
        model.setEmail(user.getUser().getEmail());

        return model;
    }

    public final GroupUserModel convertToModel(User user, Balance balance) {

        final GroupUserModel model = new GroupUserModel();
        model.setId(user.getId());
        model.setUsername(user.getUsername());
        model.setFirstName(user.getFirstName());
        model.setLastName(user.getLastName());
        model.setEmail(user.getEmail());
        model.setBalance(balance.getBalance());
        model.setCosts(balance.getCosts());
        model.setSpending(balance.getSpending());

        return model;
    }
}
