package share.costs.groups.service;

import share.costs.groups.model.GroupModel;
import share.costs.groups.rest.CreateGroupRequest;

import java.util.List;

public interface GroupService {

    GroupModel createGroup(CreateGroupRequest createGroupRequest, String username);

    GroupModel addUsers(String groupId, List<String> userIds);

    List<GroupModel> getUserGroups(String userId);

    void joinGroup(String groupId, String username);
}

