package share.costs.groups.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import share.costs.groups.entities.Group;
import share.costs.groups.entities.GroupsRepository;
import share.costs.groups.model.GroupModel;
import share.costs.groups.service.GroupService;
import share.costs.groups.service.converters.GroupConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GroupServiceImpl implements GroupService {


    private final GroupsRepository groupRepository;
    private final GroupConverter groupConverter;

    public GroupServiceImpl(GroupsRepository groupRepository, GroupConverter groupConverter) {
        this.groupRepository = groupRepository;
        this.groupConverter = groupConverter;
    }

    @Override
    public List<GroupModel> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return null;//groups.stream().map(group -> groupConverter.convertToModel(group)).collect(Collectors.toList());
    }

    @Override
    public GroupModel createGroup(GroupModel groupModel) {
        log.info("Create group BEGIN: {}", groupModel);

        final Group entity = groupConverter.convertToEntity(groupModel);

        final Group group = groupRepository.save(entity);

        final GroupModel created = groupConverter.convertToModel(group);

        log.info("Create group END: {}", created);

        return created;
    }

    @Override
    public void deleteGroup(String groupId) {

    }

    @Override
    public void addUser(String userId) {

    }

    @Override
    public void deleteUser(String userId) {

    }
}
