package share.costs.groups.service;

import share.costs.groups.model.GroupModel;
import share.costs.groups.rest.AddUserRequest;
import share.costs.groups.rest.CreateGroupRequest;
import share.costs.groups.rest.RemoveUserRequest;
import share.costs.users.model.UserModel;

import java.util.List;

public interface GroupService {

    GroupModel createGroup(CreateGroupRequest createGroupRequest, String username);

    GroupModel addUser(AddUserRequest request);

    List<GroupModel> getUserGroups(String userId);

    GroupModel joinGroup(String groupId, String username);

    GroupModel getGroup(final String groupId);

    List<UserModel> findUsers(String groupId, String searchValue);

    GroupModel removeGroupPendingUser(RemoveUserRequest request);
}

