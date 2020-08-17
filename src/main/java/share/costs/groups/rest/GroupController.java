package share.costs.groups.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import share.costs.groups.model.GroupModel;
import share.costs.groups.service.GroupService;
import share.costs.users.model.UserModel;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(final GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create")
    public GroupModel createGroup(@RequestBody final CreateGroupRequest createGroupRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return groupService.createGroup(createGroupRequest, username);
    }

    @PostMapping("/join")
    public GroupModel joinGroup(@RequestParam final String groupId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return groupService.joinGroup(groupId, username);
    }

    @PostMapping("/add-user")
    public GroupModel addGroupUsers(@RequestBody final AddUserRequest request) {
        return groupService.addUser(request);
    }

    @PostMapping("/remove-user")
    public GroupModel removeGroupPendingUser(@RequestBody final RemoveUserRequest request) {
        return groupService.removeGroupPendingUser(request);
    }

    @PostMapping("/get")
    public GroupModel getGroup(@RequestParam final String groupId) {
        return groupService.getGroup(groupId);
    }

    @PostMapping("/get-all")
    public List<GroupModel> getGroups() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return groupService.getUserGroups(username);
    }

    @GetMapping("/{userId}")
    public List<GroupModel> getUserGroups(@PathVariable String userId) {
        return groupService.getUserGroups(userId);
    }

    @PostMapping("/find-new-members")
    public List<UserModel> findUsers(@RequestParam String groupId, @RequestParam String value) {
        if(value.length() < 2) {
            return null;
        }
        return groupService.findUsers(groupId, value);
    }
}
