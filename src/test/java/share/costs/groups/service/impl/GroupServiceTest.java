package share.costs.groups.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import share.costs.groups.converters.GroupConverter;
import share.costs.groups.entities.Group;
import share.costs.groups.entities.GroupUserBalanceRepository;
import share.costs.groups.entities.GroupsRepository;
import share.costs.groups.model.GroupModel;
import share.costs.groups.rest.AddUserRequest;
import share.costs.groups.rest.CreateGroupRequest;
import share.costs.users.converters.GroupUserConverter;
import share.costs.users.converters.UserConverter;
import share.costs.users.entities.PendingUser;
import share.costs.users.entities.PendingUserRepository;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    private GroupServiceImpl groupService;
    private GroupUserConverter groupUserConverter;
    private UserConverter userConverter;
    private GroupConverter groupConverter;

    @Mock
    GroupsRepository mockGroupsRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private PendingUserRepository mockPendingUserRepository;
    @Mock
    private GroupUserBalanceRepository mockGroupUserBalanceRepository;

    @BeforeEach
    public void setUp(){


        groupUserConverter = new GroupUserConverter();
        groupConverter = new GroupConverter(groupUserConverter);
        userConverter = new UserConverter();

        groupService = new GroupServiceImpl(mockGroupsRepository,
                groupConverter,
                mockUserRepository,
                mockPendingUserRepository,
                mockGroupUserBalanceRepository,
                groupUserConverter,
                userConverter);
    }

    @Test
    public void testFindUserGroups(){

        // Arrange
        UserEntity ownerEntity = new UserEntity();
        ownerEntity.setId("ownerId");
        ownerEntity.setEmail("owner@emailcom");
        ownerEntity.setFirstName("Owner");
        ownerEntity.setLastName("Owner");
        ownerEntity.setPassword("password");
        ownerEntity.setImage(null);

        UserEntity userEntity = new UserEntity();
        ownerEntity.setId("userId");
        userEntity.setEmail("user@emailcom");
        userEntity.setFirstName("First name");
        userEntity.setLastName("Last name");
        userEntity.setPassword("password");
        userEntity.setImage(null);

        UserEntity pendingUserEntity = new UserEntity();
        ownerEntity.setId("pendingUserId");
        pendingUserEntity.setEmail("user@emailcom");
        pendingUserEntity.setFirstName("Pending");
        pendingUserEntity.setLastName("Pending");
        pendingUserEntity.setPassword("password");
        pendingUserEntity.setImage(null);

        PendingUser pendingUser = new PendingUser();
        pendingUser.setUser(pendingUserEntity);

        Group group = new Group();
        group.setId("0");
        group.setName("Group Name");
        group.setDescription("");
        group.setOwner(ownerEntity);
        group.setUsers(List.of(userEntity));
        group.setPendingUsers(List.of(pendingUser));

        Group pendingGroup = new Group();
        pendingGroup.setId("1");
        pendingGroup.setName("Pending Group Name");
        pendingGroup.setDescription("");
        pendingGroup.setOwner(ownerEntity);
        pendingGroup.setUsers(List.of(userEntity));
        pendingGroup.setPendingUsers(List.of(pendingUser));


        when(mockUserRepository.findOneByEmail(any())).
                thenAnswer((Answer<Optional<UserEntity>>) invocation -> {
                    final String searchValue = invocation.getArgument(0);
                    if(searchValue.equals(ownerEntity.getEmail())){
                        return Optional.of(ownerEntity);
                    }
                    if(searchValue.equals(pendingUserEntity.getEmail())){
                        return Optional.of(ownerEntity);
                    }
                    if(searchValue.equals(userEntity.getEmail())){
                        return Optional.of(userEntity);
                    }

                    return Optional.empty();
                });

        List<Group> groupsList = new ArrayList<Group>();
        groupsList.add(group);
        List<Group> pendingGroupsList = new ArrayList<Group>();
        pendingGroupsList.add(pendingGroup);
        when(mockGroupsRepository.findGroupsByPendingUsersUserId(any())).thenReturn(groupsList);
        when(mockGroupsRepository.findGroupsByUsersId(any())).thenReturn(pendingGroupsList);

        // Action
        List<GroupModel> found = groupService.findUserGroups(ownerEntity.getEmail());

        Assertions.assertNotNull(group);
        Assertions.assertEquals(2, found.size());
        Assertions.assertEquals(pendingGroup.getId(), found.get(0).getId());
        Assertions.assertEquals(pendingGroup.getName(), found.get(0).getName());
        Assertions.assertEquals(pendingGroup.getDate(), found.get(0).getDate());
        Assertions.assertEquals(group.getId(), found.get(1).getId());
        Assertions.assertEquals(group.getName(), found.get(1).getName());
        Assertions.assertEquals(group.getDate(), found.get(1).getDate());
    }

    @Test
    public void testCreateGroup(){

        // Arrange
        UserEntity ownerEntity = new UserEntity();
        ownerEntity.setId("ownerId");
        ownerEntity.setEmail("owner@emailcom");
        ownerEntity.setFirstName("Owner");
        ownerEntity.setLastName("Owner");
        ownerEntity.setPassword("password");
        ownerEntity.setImage(null);

        when(mockUserRepository.findOneByEmail(ownerEntity.getEmail())).
                thenReturn(Optional.of(ownerEntity));

        CreateGroupRequest request = new CreateGroupRequest();
        request.setName("Test group name");
        request.setDescription("Test group description");

        // Action
        final GroupModel group = groupService.createGroup(request, ownerEntity.getEmail());

        // Asserts
        Assertions.assertNotNull(group);
        Assertions.assertEquals(group.getName(), request.getName());
        Assertions.assertEquals(group.getDescription(), request.getDescription());
        Assertions.assertEquals(1, group.getUsers().size());
        Assertions.assertEquals(ownerEntity.getEmail(), group.getUsers().get(0).getUsername());
        Assertions.assertEquals(ownerEntity.getId(), group.getUsers().get(0).getId());
        Assertions.assertEquals(ownerEntity.getFirstName(), group.getUsers().get(0).getFirstName());
        Assertions.assertEquals(ownerEntity.getLastName(), group.getUsers().get(0).getLastName());
    }

    @Test
    public void testAddUsersToGroup(){

        // Arrange
        UserEntity ownerEntity = new UserEntity();
        ownerEntity.setId("owner-id");
        ownerEntity.setEmail("owner@emailcom");
        ownerEntity.setFirstName("Owner");
        ownerEntity.setLastName("Owner");
        ownerEntity.setPassword("password");
        ownerEntity.setImage(null);

        UserEntity pendingUserEntity = new UserEntity();
        pendingUserEntity.setId("pending-user-id");
        pendingUserEntity.setEmail("user@emailcom");
        pendingUserEntity.setFirstName("Pending");
        pendingUserEntity.setLastName("Pending");
        pendingUserEntity.setPassword("password");
        pendingUserEntity.setImage(null);

        Group group = new Group();
        group.setId("group-id");
        group.setName("Group name");
        group.setDescription("Group description");
        group.setOwner(ownerEntity);
        group.setUsers(List.of(ownerEntity));
        group.setBalance(BigDecimal.ZERO);

        when(mockUserRepository.existsById(any())).
                thenReturn(true);
        when(mockUserRepository.findById(pendingUserEntity.getId())).
                thenReturn(Optional.of(pendingUserEntity));

        when(mockGroupsRepository.existsById(any())).
                thenReturn(true);
        when(mockGroupsRepository.findById(group.getId())).
                thenReturn(Optional.of(group));
        when(mockGroupsRepository.save(any(Group.class))).
                thenAnswer((Answer<Group>) invocation -> invocation.getArgument(0));
        when(mockPendingUserRepository.save(any(PendingUser.class))).
                thenAnswer((Answer<PendingUser>) invocation -> invocation.getArgument(0));
        AddUserRequest addUserRequest = new AddUserRequest().
                setGroupId(group.getId()).
                setUserId(pendingUserEntity.getId());
        // Action
       final GroupModel groupModel = groupService.addUser(addUserRequest);

        // Asserts
        Assertions.assertNotNull(groupModel);
        Assertions.assertEquals(groupModel.getName(), group.getName());
        Assertions.assertEquals(1, group.getUsers().size());
        Assertions.assertEquals(1, group.getPendingUsers().size());
        final UserEntity actualPendingUser = group.getPendingUsers().get(0).getUser();
        Assertions.assertEquals(pendingUserEntity.getEmail(), actualPendingUser.getEmail());
        Assertions.assertEquals(pendingUserEntity.getFirstName(), actualPendingUser.getFirstName());
        Assertions.assertEquals(pendingUserEntity.getLastName(), actualPendingUser.getLastName());
    }
}
