package share.costs.users.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import share.costs.config.security.PasswordEncoder;
import share.costs.config.security.TokenProvider;
import share.costs.users.entities.AuthorityEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    HttpServletResponse response;

    private Long USER1_ID, USER2_ID;
    private String USER1_EMAIL="user1@email.com", USER2_EMAIL="user2@email.com";
    private String USER1_FNAME="John", USER2_FNAME="Anna";
    private String USER1_LNAME="Smith", USER2_LNAME="Lenard";
    private String USER1_PASSWORD = "123", USER2_PASSWORD="456";
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp(){

        passwordEncoder = new PasswordEncoder();
        userRepository.deleteAll();

        UserEntity user1 = new UserEntity();
        user1.setEmail(USER1_EMAIL);
        user1.setFirstName(USER1_FNAME);
        user1.setLastName(USER1_LNAME);
        user1.setPassword(passwordEncoder.hashPassword(USER1_PASSWORD));

        AuthorityEntity user1Role = new AuthorityEntity().setRole("ROLE_USER");
        user1.setRoles(List.of(user1Role));
        user1 = userRepository.save(user1);
        USER1_ID = user1.getId();

        UserEntity user2 = new UserEntity();
        user2.setEmail(USER2_EMAIL);
        user2.setFirstName(USER2_FNAME);
        user2.setLastName(USER2_LNAME);
        user2.setPassword(passwordEncoder.hashPassword(USER2_PASSWORD));
        AuthorityEntity user2Role = new AuthorityEntity().setRole("ROLE_ADMIN");
        user2.setRoles(List.of(user2Role));
        user2 = userRepository.save(user2);
        user2 = userRepository.save(user2);
        USER2_ID = user2.getId();
    }

    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
    }

}
