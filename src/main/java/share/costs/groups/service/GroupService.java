package share.costs.groups.service;

import share.costs.groups.model.GroupModel;
import share.costs.groups.rest.AddUserRequest;
import share.costs.groups.rest.CreateGroupRequest;
import share.costs.groups.rest.RemoveUserRequest;
import share.costs.users.model.UserModel;

import java.util.List;

public interface GroupService {

    GroupModel createGroup(CreateGroupRequest createGroupRequest, String ownerEmail);

    GroupModel addUser(AddUserRequest request);

    List<GroupModel> findUserGroups(String userId);

    GroupModel joinGroup(Long groupId, String username);

    GroupModel getGroup(final Long groupId);

    List<UserModel> findUsers(Long groupId, String searchValue);

    GroupModel removeGroupPendingUser(RemoveUserRequest request);

    List<GroupModel> findGroupsByUserId(Long userId);
}

