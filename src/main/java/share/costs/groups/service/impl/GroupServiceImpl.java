package share.costs.groups.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.costs.balances.entities.Balance;
import share.costs.balances.entities.GroupUserBalance;
import share.costs.balances.entities.GroupUserBalanceRepository;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.groups.entities.Group;
import share.costs.groups.entities.GroupsRepository;
import share.costs.groups.model.GroupModel;
import share.costs.groups.rest.CreateGroupRequest;
import share.costs.groups.service.GroupService;
import share.costs.groups.service.converters.GroupConverter;
import share.costs.users.entities.User;
import share.costs.users.entities.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GroupServiceImpl implements GroupService {

    private final GroupsRepository groupRepository;
    private final GroupConverter groupConverter;
    private final UserRepository userRepository;
    private final GroupUserBalanceRepository groupUserBalanceRepository;

    public GroupServiceImpl(final GroupsRepository groupRepository,
                            final GroupConverter groupConverter,
                            final UserRepository userRepository,
                            GroupUserBalanceRepository groupUserBalanceRepository) {
        this.groupRepository = groupRepository;
        this.groupConverter = groupConverter;
        this.userRepository = userRepository;
        this.groupUserBalanceRepository = groupUserBalanceRepository;
    }

    @Override
    @Transactional
    public List<GroupModel> getUserGroups(String userId) {
        log.info("Get group BEGIN: {} -> ", userId);

        if (!userRepository.existsById(userId)) {
            throw new HttpBadRequestException("User entity does not exist for id: " + userId);
        }

        List<Group> groups = groupRepository.findGroupsByUsersId(userId);
        final List<GroupModel> groupModels = groups.stream()
                                    .map(group -> groupConverter.convertToModel(group))
                                    .collect(Collectors.toList());

        log.info("Get group END: {} -> ", groupModels);

        return groupModels;
    }

    @Override
    @Transactional
    public GroupModel createGroup(CreateGroupRequest createGroupRequest, String username) {

        log.info("Create group BEGIN: {} -> %s user: %s", createGroupRequest, username);

        Optional<User> ownerOpt = userRepository.findByUsername(username);

        if( !ownerOpt.isPresent() ) {
            throw new HttpBadRequestException("User entity does not exist for username:" + username);
        }

        final User owner = ownerOpt.get();
        final Group group = new Group();
        group.setOwner(owner);
        group.setName(createGroupRequest.getName());

        List<User> pendingUsers = createGroupRequest.getUserIds().stream()
                .distinct()
                .map(value -> userRepository.findById(value))
                .filter(userOpt -> userOpt.isPresent())
                .map(userOpt -> userOpt.get())
                .collect(Collectors.toList());

        group.getUsers().add(owner);
        group.getPendingUsers().addAll(pendingUsers);

        groupRepository.save(group);

        createBalance(owner, group);

        final GroupModel created = groupConverter.convertToModel(group);

        log.info("Create group END: {}", created);

        return created;
    }

    @Override
    @Transactional
    public GroupModel addUsers(final String groupId, final List<String> userIds) {
        if (!groupRepository.existsById(groupId)) {
            throw new HttpBadRequestException("Group entity does not exist for id: " + groupId);
        }

        final Group group = groupRepository.findById(groupId).get();
        log.info("Create update BEGIN: {} -> ", group);

        userIds.stream()
                .distinct()
                .map(value -> userRepository.findById(value))
                .filter(userOpt -> userOpt.isPresent())
                .map(userOpt -> userOpt.get())
                .filter(user -> !group.getUsers().contains(user))
                .forEach(user -> group.getUsers().add(user));

        final GroupModel updated = groupConverter.convertToModel(groupRepository.save(group));
        log.info("Create update END: {} -> ", updated);
        return updated;
    }

    @Override
    @Transactional
    public void joinGroup(String groupId, String username ){
        if (!groupRepository.existsById(groupId)) {
            throw new HttpBadRequestException("Group entity does not exist for id: " + groupId);
        }

        if(!userRepository.existsUserByUsername(username)) {
            throw new HttpBadRequestException("User entity does not exist for username: " + username);
        }

        final Group group = groupRepository.findById(groupId).get();
        log.info("Join to group BEGIN: {} -> ", group);

        final User user = userRepository.findByUsername(username).get();
        log.info("Joining to group new user: {} -> ", user);

        if(group.getPendingUsers().contains(user)) {
            group.getPendingUsers().remove(user);
        }

        if(group.getUsers().contains(user)) {
            throw new HttpBadRequestException("User is already joined " + group.getName());
        }

        group.getUsers().add(user);

        createBalance(user, group);
    }

    /**
     *
     * @param user User entity
     * @param group Group entity
     * @return the saved entity, including the updated id field
     */
    private GroupUserBalance createBalance(User user, Group group) {
        GroupUserBalance balance = new GroupUserBalance();
        balance.setUser(user);
        balance.setGroup(group);
        balance.setBalance(new Balance());
        return groupUserBalanceRepository.save(balance);
    }
}
