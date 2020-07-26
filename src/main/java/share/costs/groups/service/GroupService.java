package share.costs.groups.service;

import share.costs.groups.model.GroupModel;

import java.util.List;

public interface GroupService {

    List<GroupModel> getAllGroups();
    GroupModel createGroup(GroupModel group);
    void deleteGroup(String groupId);
    void addUser(String userId);
    void deleteUser(String userId);
}

