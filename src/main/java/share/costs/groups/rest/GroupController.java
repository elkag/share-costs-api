package share.costs.groups.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import share.costs.groups.model.GroupModel;
import share.costs.groups.service.GroupService;
import share.costs.users.model.UserModel;

import java.security.Principal;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/groups")
@PreAuthorize("hasRole('USER')")
public class GroupController {

    private final GroupService groupService;

    public GroupController(final GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create")
    public GroupModel createGroup(@RequestBody final CreateGroupRequest createGroupRequest, Principal principal) {
        return groupService.createGroup(createGroupRequest, principal.getName());
    }

    @PostMapping("/join")
    public GroupModel joinGroup(@RequestParam final String groupId, Principal principal) {
        return groupService.joinGroup(groupId, principal.getName());
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
    public List<GroupModel> getGroups(Principal principal) {

        return groupService.getUserGroups(principal.getName());
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
