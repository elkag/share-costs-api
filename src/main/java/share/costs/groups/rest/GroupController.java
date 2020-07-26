package share.costs.groups.rest;

import lombok.extern.log4j.Log4j2;
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

    @PostMapping("/")
    public GroupModel createGroup(@RequestBody final GroupModel groupModel) {
        return groupService.createGroup(groupModel);
    }

    @GetMapping("/")
    public List<GroupModel> getGroups() {
        return groupService.getAllGroups();
    }
}
