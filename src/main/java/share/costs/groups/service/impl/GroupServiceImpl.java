package share.costs.groups.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.costs.balances.entities.Balance;
import share.costs.groups.entities.GroupUserBalance;
import share.costs.groups.entities.GroupUserBalanceRepository;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.groups.entities.Group;
import share.costs.groups.entities.GroupsRepository;
import share.costs.groups.model.GroupModel;
import share.costs.groups.rest.CreateGroupRequest;
import share.costs.groups.service.GroupService;
import share.costs.groups.service.converters.GroupConverter;
import share.costs.users.entities.User;
import share.costs.users.entities.UserRepository;
import share.costs.users.model.GroupUserModel;
import share.costs.users.service.converters.GroupUserConverter;

import java.util.ArrayList;
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
    private final GroupUserConverter groupUserConverter;

    public GroupServiceImpl(final GroupsRepository groupRepository,
                            final GroupConverter groupConverter,
                            final UserRepository userRepository,
                            GroupUserBalanceRepository groupUserBalanceRepository,
                            GroupUserConverter groupUserConverter) {
        this.groupRepository = groupRepository;
        this.groupConverter = groupConverter;
        this.userRepository = userRepository;
        this.groupUserBalanceRepository = groupUserBalanceRepository;
        this.groupUserConverter = groupUserConverter;
    }

    @Override
    @Transactional
    public GroupModel getGroup(final String groupId) {

        if (!groupRepository.existsById(groupId)) {
            throw new HttpBadRequestException("Group entity does not exist for id: " + groupId);
        }

        final Group group = groupRepository.findById(groupId).get();
        log.info("Get group BEGIN: {} -> ", group);

        List<GroupUserModel> groupUserModels = group.getUsers().stream().map(user -> {
            final Optional<GroupUserBalance> gubOpt = groupUserBalanceRepository.findByUserAndGroup(user, group);
            return gubOpt.map(groupUserBalance -> groupUserConverter.convertToModel(user, groupUserBalance.getBalance())).orElse(null);
        }).collect(Collectors.toList());

        final GroupModel groupModel = groupConverter.convertToModel(group);
        groupModel.setUsers(groupUserModels);

        log.info("Get group END: {} -> ", groupModel);

        return groupModel;
    }

    @Override
    @Transactional
    public List<GroupModel> getUserGroups(String username) {
        log.info("Get group BEGIN: {} -> ", username);

        if (!userRepository.existsUserByUsername(username)) {
            throw new HttpBadRequestException("User entity does not exist for id: " + username);
        }

        final User user = userRepository.findByUsername(username).get();

        final List<Group> groups = groupRepository.findGroupsByUsersId(user.getId());

        final List<GroupModel> models = new ArrayList<GroupModel>();

        groups.forEach(group -> {
            GroupModel model = groupConverter.convertToModel(group);
            List<GroupUserModel> groupUsers = new ArrayList<GroupUserModel>();

            group.getUsers().forEach(currentUser -> {
                final Optional<GroupUserBalance> gubOpt = groupUserBalanceRepository.findByUserAndGroup(currentUser, group);
                if(!gubOpt.isPresent())
                    return;
                groupUsers.add(groupUserConverter.convertToModel(currentUser, gubOpt.get().getBalance()));

            });
            model.setUsers(groupUsers);
            models.add(model);
        });

        log.info("Get group END: {} -> ", models);

        return models;
    }

    @Override
    @Transactional
    public GroupModel createGroup(CreateGroupRequest createGroupRequest, String username) {

        log.info("Create group BEGIN: {} -> %s user: %s", createGroupRequest);

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
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
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
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(user -> !group.getPendingUsers().contains(user))
                .forEach(user -> group.getPendingUsers().add(user));

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

        group.getPendingUsers().remove(user);

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
     */
    @Transactional
    private void createBalance(User user, Group group) {
        GroupUserBalance balance = new GroupUserBalance();
        balance.setUser(user);
        balance.setGroup(group);
        balance.setBalance(new Balance());
        groupUserBalanceRepository.save(balance);
    }
}
