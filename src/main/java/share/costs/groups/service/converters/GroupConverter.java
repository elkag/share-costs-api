package share.costs.groups.service.converters;

import org.springframework.stereotype.Component;
import share.costs.balances.entities.Balance;
import share.costs.groups.entities.Group;
import share.costs.groups.model.GroupModel;
import share.costs.users.service.converters.GroupUserConverter;
import share.costs.users.service.converters.UserConverter;

import java.util.stream.Collectors;

@Component
public class GroupConverter {

    final GroupUserConverter groupUserConverter;

    public GroupConverter(GroupUserConverter userConverter) {
        this.groupUserConverter = userConverter;
    }

    public Group convertToEntity(GroupModel model) {
        if(model == null) {
            return null;
        }
        final Group group = new Group();
        group.setId(model.getId());
        group.setName(model.getName());
        //group.setOwner(groupUserConverter.convertToEntity(model.getOwner()));
        /*group.setUsers(model.getUsers().stream()
                .map(userModel -> userConverter.convertToEntity(userModel))
                .collect(Collectors.toList()));*/


        return group;
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
                .map(user -> groupUserConverter.convertToModel(user))
                .collect(Collectors.toList()));

        model.setUsers(group.getUsers().stream()
                .map(user -> groupUserConverter.convertToModel(user, new Balance()))
                .collect(Collectors.toList()));


        return model;
    }
}
