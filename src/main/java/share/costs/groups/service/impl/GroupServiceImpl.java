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
import share.costs.groups.rest.AddUserRequest;
import share.costs.groups.rest.CreateGroupRequest;
import share.costs.groups.rest.RemoveUserRequest;
import share.costs.groups.service.GroupService;
import share.costs.groups.service.converters.GroupConverter;
import share.costs.users.entities.PendingUser;
import share.costs.users.entities.PendingUserRepository;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;
import share.costs.users.model.GroupUserModel;
import share.costs.users.model.UserModel;
import share.costs.users.service.converters.GroupUserConverter;
import share.costs.users.service.converters.UserConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GroupServiceImpl implements GroupService {

    private final GroupsRepository groupRepository;
    private final GroupConverter groupConverter;
    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;
    private final GroupUserBalanceRepository groupUserBalanceRepository;
    private final GroupUserConverter groupUserConverter;
    private final UserConverter userConverter;

    public GroupServiceImpl(final GroupsRepository groupRepository,
                            final GroupConverter groupConverter,
                            final UserRepository userRepository,
                            PendingUserRepository pendingUserRepository,
                            GroupUserBalanceRepository groupUserBalanceRepository,
                            GroupUserConverter groupUserConverter,
                            UserConverter userConverter) {
        this.groupRepository = groupRepository;
        this.groupConverter = groupConverter;
        this.userRepository = userRepository;
        this.pendingUserRepository = pendingUserRepository;
        this.groupUserBalanceRepository = groupUserBalanceRepository;
        this.groupUserConverter = groupUserConverter;
        this.userConverter = userConverter;
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
        }).filter(Objects::nonNull).collect(Collectors.toList());

        final GroupModel groupModel = groupConverter.convertToModel(group);
        groupModel.setUsers(groupUserModels);

        log.info("Get group END: {} -> ", groupModel);

        return groupModel;
    }

    @Override
    @Transactional
    public List<GroupModel> getUserGroups(String email) {
        log.info("Get group BEGIN: {} -> ", email);

        final UserEntity user = userRepository.findOneByEmail(email).get();

        final List<Group> groups = groupRepository.findGroupsByUsersId(user.getId());
        final List<Group> pendingGroups = groupRepository.findGroupsByPendingUsersUserId(user.getId());

        groups.addAll(pendingGroups);
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
    public GroupModel createGroup(CreateGroupRequest createGroupRequest, String email) {

        log.info("Create group BEGIN: {} -> %s user: %s", createGroupRequest);

        Optional<UserEntity> ownerOpt = userRepository.findOneByEmail(email);

        if( !ownerOpt.isPresent() ) {
            throw new HttpBadRequestException("User entity does not exist for username:" + email);
        }

        final UserEntity owner = ownerOpt.get();
        final Group group = new Group();
        group.setOwner(owner);
        group.setName(createGroupRequest.getName());
        group.setDescription(createGroupRequest.getDescription());

        group.getUsers().add(owner);

        groupRepository.save(group);

        createBalance(owner, group);

        final GroupModel created = groupConverter.convertToModel(group);

        log.info("Create group END: {}", created);

        return created;
    }


    @Transactional
    @Override
    public List<UserModel> findUsers(String groupId, String searchValue) {

        if(!groupRepository.findById(groupId).isPresent()) {
            throw new HttpBadRequestException("error");
        }
        List<UserModel> users = new ArrayList<>();

        List<UserEntity> existsUsers = groupRepository.findById(groupId).get().getUsers();

        Group g = groupRepository.findById(groupId).get();

        (userRepository.findByFirstNameIgnoreCaseStartsWith(searchValue)).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(u -> !existsUsers.contains(u))
                .map(userConverter::convertToModel)
                .forEach(users::add);

        (userRepository.findByLastNameIgnoreCaseStartsWith(searchValue)).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(u -> !existsUsers.contains(u))
                .map(userConverter::convertToModel)
                .filter(value -> !users.contains(value))
                .forEach(users::add);


        (userRepository.findByEmailIgnoreCaseContains(searchValue)).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(u -> !existsUsers.contains(u))
                .map(userConverter::convertToModel)
                .filter(value -> !users.contains(value))
                .forEach(users::add);

        return users;
    }

    @Override
    public GroupModel removeGroupPendingUser(RemoveUserRequest request) {

        final String userId = request.getUserId();
        final String groupId = request.getGroupId();
        if (!groupRepository.existsById(groupId)) {
            throw new HttpBadRequestException("Group entity does not exist for id: " + groupId);
        }

        if (!userRepository.existsById(userId)) {
            throw new HttpBadRequestException("User entity does not exist for id: " + groupId);
        }

        final Group group = groupRepository.findById(groupId).get();
        log.info("Create update BEGIN: {} -> ", group);

        final UserEntity user = userRepository.findById(userId).get();

        final List<PendingUser> pendings = group.getPendingUsers().stream().filter(u -> !u.getUser().getId().equals(user.getId())).collect(Collectors.toList());

        group.setPendingUsers(pendings);

        final GroupModel updated = groupConverter.convertToModel(groupRepository.save(group));

        log.info("Create update END: {} -> ", updated);
        return updated;
    }

    @Override
    @Transactional
    public GroupModel addUser(final AddUserRequest request) {
        final String groupId = request.getGroupId();
        final String userId = request.getUserId();

        if (!groupRepository.existsById(groupId)) {
            throw new HttpBadRequestException("Group entity does not exist for id: " + groupId);
        }

        final Group group = groupRepository.findById(groupId).get();
        log.info("Update BEGIN: {} -> ", group);

        final UserEntity user = userRepository.findById(userId).get();
        final boolean isExist;
        isExist = group.getPendingUsers().contains(user);
        if(!isExist) {
            final PendingUser pending = new PendingUser();
            pending.setUser(user);
            PendingUser updated = pendingUserRepository.save(pending);
            group.getPendingUsers().add(updated);
        }

        final Group saved = groupRepository.save(group);
        final GroupModel updated = groupConverter.convertToModel(saved);
        log.info("Create update END: {} -> ", updated);
        return updated;
    }

    @Override
    @Transactional
    public GroupModel joinGroup(String groupId, String email ){
        if (!groupRepository.existsById(groupId)) {
            throw new HttpBadRequestException("Group entity does not exist for id: " + groupId);
        }

        final Group group = groupRepository.findById(groupId).get();
        log.info("Join to group BEGIN: {} -> ", group);

        final UserEntity user = userRepository.findOneByEmail(email).get();
        log.info("Joining to group new user: {} -> ", user);

        final List<PendingUser> pendingList = pendingUserRepository.findByUserId(user.getId());

        Optional<PendingUser> pendingOpt = pendingList.stream().filter(group.getPendingUsers()::contains).findAny();

        if(pendingOpt == null || !pendingOpt.isPresent() || !group.getPendingUsers().contains(pendingOpt.get())) {
            throw new HttpBadRequestException("User is not pending for group " + group.getName());
        }

        group.getPendingUsers().remove(pendingOpt.get());

        if(group.getUsers().contains(user)) {
            throw new HttpBadRequestException("User is already joined " + group.getName());
        }

        group.getUsers().add(user);

        createBalance(user, group);

        return groupConverter.convertToModel(groupRepository.save(group));

    }

    /**
     *
     * @param user User entity
     * @param group Group entity
     */
    @Transactional
    private void createBalance(UserEntity user, Group group) {
        GroupUserBalance balance = new GroupUserBalance();
        balance.setUser(user);
        balance.setGroup(group);
        balance.setBalance(new Balance());
        groupUserBalanceRepository.save(balance);
    }
}
