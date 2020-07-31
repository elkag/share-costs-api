package share.costs.groups.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import share.costs.groups.model.GroupModel;
import share.costs.groups.service.GroupService;

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
    public void joinGroup(@RequestParam final String groupId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        groupService.joinGroup(groupId, username);
    }

    @PostMapping("/add-users/")
    public GroupModel addGroupUsers(@RequestParam final String groupId, @RequestParam List<String> userIds) {
        groupService.addUsers(groupId, userIds);
        return null;//groupService.createGroup(createGroupRequest, username);
    }

    @GetMapping("/")
    public List<GroupModel> getGroups() {
        return null;
    }

    @GetMapping("/{userId}")
    public List<GroupModel> getUserGroups(@PathVariable String userId) {
        return groupService.getUserGroups(userId);
    }
}
