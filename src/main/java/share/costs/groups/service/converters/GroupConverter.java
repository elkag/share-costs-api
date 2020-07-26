package share.costs.groups.service.converters;

import org.springframework.stereotype.Component;
import share.costs.groups.entities.Group;
import share.costs.groups.model.GroupModel;
import share.costs.users.service.UserConverter;

import java.util.stream.Collectors;

@Component
public class GroupConverter {

    final UserConverter userConverter;

    public GroupConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    public Group convertToEntity(GroupModel model) {
        if(model == null) {
            return null;
        }
        final Group group = new Group();
        group.setId(model.getId());
        group.setName(model.getName());
        group.setOwner(userConverter.convertToEntity(model.getOwner()));
        group.setUsers(model.getUsers().stream()
                .map(userModel -> userConverter.convertToEntity(userModel))
                .collect(Collectors.toList()));

        return group;
    }

    public GroupModel convertToModel(Group group) {
        if(group == null) {
            return null;
        }
        final GroupModel model = new GroupModel();
        model.setId(group.getId());
        model.setName(group.getName());
        model.setOwner(userConverter.convertToModel(group.getOwner()));
        model.setUsers(group.getUsers().stream()
                .map(user -> userConverter.convertToModel(user))
                .collect(Collectors.toList()));


        return model;
    }
}
