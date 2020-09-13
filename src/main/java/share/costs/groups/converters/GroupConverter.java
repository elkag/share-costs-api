package share.costs.groups.converters;

import org.springframework.stereotype.Component;
import share.costs.balances.entities.Balance;
import share.costs.groups.entities.Group;
import share.costs.groups.model.GroupModel;
import share.costs.users.converters.GroupUserConverter;

import java.util.stream.Collectors;

@Component
public class GroupConverter {

    final GroupUserConverter groupUserConverter;

    public GroupConverter(GroupUserConverter userConverter) {
        this.groupUserConverter = userConverter;
    }

    public GroupModel convertToModel(Group group) {
        if(group == null) {
            return null;
        }
        final GroupModel model = new GroupModel();
        model.setId(group.getId());
        model.setName(group.getName());
        model.setOwner(groupUserConverter.convertToModel(group.getOwner(), new Balance()));
        model.setDescription(group.getDescription());
        model.setDate(group.getDate());
        model.setStatus(group.getStatus());
        model.setBalance(group.getBalance());
        model.setPendingUsers(group.getPendingUsers().stream()
                .map(groupUserConverter::convertToModel)
                .collect(Collectors.toList()));

        model.setUsers(group.getUsers().stream()
                .map(user -> groupUserConverter.convertToModel(user, new Balance()))
                .collect(Collectors.toList()));


        return model;
    }
}
