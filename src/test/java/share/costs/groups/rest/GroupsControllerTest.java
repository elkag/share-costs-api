package share.costs.groups.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import share.costs.balances.entities.Balance;
import share.costs.config.security.PasswordEncoder;
import share.costs.groups.entities.Group;
import share.costs.groups.entities.GroupUserBalance;
import share.costs.groups.entities.GroupUserBalanceRepository;
import share.costs.groups.entities.GroupsRepository;
import share.costs.users.entities.AuthorityEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static share.costs.groups.constants.GroupConstants.GROUP_STATUS_ACTIVE;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private GroupUserBalanceRepository groupUserBalanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity user1;

    private Group newGroup;

    private final String NEW_GROUP_ID = "new-group-id";

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp(){
        passwordEncoder = new PasswordEncoder();

        user1 = new UserEntity();
        user1.setEmail("groupOwner@email.com");
        user1.setFirstName("Owner first name");
        user1.setLastName("Owner last name");
        user1.setPassword(passwordEncoder.hashPassword("123"));
        user1.setId(1l);
        AuthorityEntity user1Role = new AuthorityEntity().setRole("ROLE_USER");
        user1.setRoles(List.of(user1Role));

        userRepository.save(user1);

        newGroup = new Group();
        newGroup.setName("New Group Name");
        newGroup.setDescription("New Group Description");
        newGroup.setOwner(user1);
        newGroup.setStatus(GROUP_STATUS_ACTIVE);

        final GroupUserBalance gup = new GroupUserBalance();
        gup.setBalance(new Balance());
        gup.setGroup(newGroup);
        gup.setUser(user1);

        final Balance balance = new Balance();
        balance.setGroupUserBalance(List.of(gup));

        newGroup.setGroupUserBalances(List.of(gup));


    }

    @Test
    @WithMockUser(username = "groupOwner@email.com")
    public void testCreateNewGroup() throws Exception {
        final String groupName = "New Group";
        final String ownerEmail = "groupOwner@email.com";

        final CreateGroupRequest request = new CreateGroupRequest(groupName, ownerEmail);

        final String json = objectMapper.writeValueAsString(request);

        final MvcResult result = mockMvc.perform(post("/groups/create").
                    contentType(MediaType.APPLICATION_JSON).
                    content(json).
                    accept(MediaType.APPLICATION_JSON)).
               andExpect(status().isCreated()).andReturn();
                 /*andExpect(jsonPath("$.date", is())).
                andExpect(jsonPath("$.status", is())).
                andExpect(jsonPath("$.balance", is())).
                andExpect(jsonPath("$.description", is("ROLE_USER"))).
                andExpect(jsonPath("$.users[0].email", is(ownerEmail))).
                andExpect(jsonPath("$.pendingUsers", is(nullValue())));

                 */

        System.out.println("Result ----- " + result.getResponse().getContentAsString());
    }
}
