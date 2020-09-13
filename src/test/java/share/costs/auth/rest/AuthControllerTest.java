package share.costs.auth.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import share.costs.config.security.PasswordEncoder;
import share.costs.users.entities.AuthorityEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;
import share.costs.auth.model.LoginModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import share.costs.auth.model.RegistrationRequest;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static  org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity user1;
    private UserEntity user2;
    private UserEntity newUser;

    private String USER1_EMAIL="user1@email.com", USER2_EMAIL="user2@email.com", NEW_USER_EMAIL="new@email.com";
    private String USER1_FNAME="John", USER2_FNAME="Anna", NEW_USER_FNAME="New";
    private String USER1_LNAME="Smith", USER2_LNAME="Lenard", NEW_USER_LNAME="User";
    private String USER1_PASSWORD = "123", USER2_PASSWORD="456", NEW_USER_PASSWORD="supersecret";

    private String NEW_USER_ID = "new-user-id";
    private String NON_EXISTING_USER_ID = "non-existing-user-id";
    private String USER2_ID = "user-two-id";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp(){

        userRepository.deleteAll();

        PasswordEncoder passwordEncoder = new PasswordEncoder();
        // User entities
        //-------------------------
        newUser = new UserEntity();
        newUser.setEmail(NEW_USER_EMAIL);
        newUser.setFirstName(NEW_USER_FNAME);
        newUser.setLastName(NEW_USER_LNAME);
        newUser.setPassword(NEW_USER_PASSWORD);

        AuthorityEntity newUserRole = new AuthorityEntity().setRole("ROLE_USER");
        newUser.setRoles(List.of(newUserRole));

        user1 = new UserEntity();
        user1.setEmail(USER1_EMAIL);
        user1.setFirstName(USER1_FNAME);
        user1.setLastName(USER1_LNAME);
        user1.setPassword(passwordEncoder.hashPassword((USER1_PASSWORD)));
        String USER1_ID = "user-one-id";
        user1.setId(USER1_ID);
        AuthorityEntity user1Role = new AuthorityEntity().setRole("ROLE_USER");
        user1.setRoles(List.of(user1Role));

        userRepository.save(user1);

        user2 = new UserEntity();
        user2.setEmail(USER2_EMAIL);
        user2.setFirstName(USER2_FNAME);
        user2.setLastName(USER2_LNAME);
        user2.setPassword(USER2_PASSWORD);
        user2.setId(USER2_ID);
        AuthorityEntity user2RoleUser = new AuthorityEntity().setRole("ROLE_USER");
        AuthorityEntity user2RoleAdmin = new AuthorityEntity().setRole("ROLE_ADMIN");
        user2.setRoles(List.of(user2RoleUser, user2RoleAdmin));

        userRepository.save(user2);
    }

    @Test
    @WithMockUser(username = "user")
    public void testValidateUser() throws Exception {
        this.mockMvc.perform(
                post("/auth/validate").
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());
    }

    @Test
    public void testLoginUserAndValidateTokenSuccess() throws Exception {

        final LoginModel loginModel = new LoginModel();
        loginModel.setEmail(user1.getEmail());
        loginModel.setPassword(USER1_PASSWORD);

        String json = objectMapper.writeValueAsString(loginModel);

        MvcResult result = this.mockMvc.perform(
                post("/auth/login").
                        contentType(MediaType.APPLICATION_JSON).
                        content(json).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(header().exists("x-token")).
                andExpect(header().exists("Access-Control-Allow-Headers")).
                andExpect(header().string("Access-Control-Allow-Headers", "x-token")).
                andExpect(header().exists("Access-Control-Expose-Headers")).
                andExpect(header().string("Access-Control-Expose-Headers", "x-token")).
                andReturn();

        String token = result.getResponse().getHeader("x-token");

        this.mockMvc.perform(
                post("/auth/validate").
                        contentType(MediaType.APPLICATION_JSON).
                        header("Authorization", "Bearer " + token)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.email", is(user1.getEmail()))).
                andExpect(jsonPath("$.firstName", is(user1.getFirstName()))).
                andExpect(jsonPath("$.lastName", is(user1.getLastName()))).
                andExpect(jsonPath("$.roles[0].role", is("ROLE_USER"))).
                andExpect(jsonPath("$.password", is(nullValue())));
    }


    @Test
    public void testLoginUserFail() throws Exception {

        final String wrongPassword = "wrong_password";
        final LoginModel loginModel = new LoginModel();
        loginModel.setEmail(USER1_EMAIL);
        loginModel.setPassword(wrongPassword);

        String json = objectMapper.writeValueAsString(loginModel);

        this.mockMvc.perform(
                post("/auth/login").
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON).
                        content(json)).
                andExpect(status().isForbidden());
    }

    @Test
    public void testValidateTokenSuccess() throws Exception {

        final LoginModel loginModel = new LoginModel();
        loginModel.setEmail(USER1_EMAIL);
        loginModel.setPassword(USER1_PASSWORD);

        String json = objectMapper.writeValueAsString(loginModel);

        MvcResult result = this.mockMvc.perform(
                post("/auth/login").
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON).
                        content(json)).
                andReturn();

        String token = result.getResponse().getHeader("x-token");

        this.mockMvc.perform(
                post("/auth/validate").
                        contentType(MediaType.APPLICATION_JSON).
                        header("Authorization", "Bearer " + token)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.email", is(USER1_EMAIL))).
                andExpect(jsonPath("$.firstName", is(USER1_FNAME))).
                andExpect(jsonPath("$.lastName", is(USER1_LNAME))).
                andExpect(jsonPath("$.roles[0].role", is("ROLE_USER")));
    }

    @Test
    public void testCreateUserSuccess() throws Exception {

        RegistrationRequest registrationModel = createRegistrationModel();

        String json = objectMapper.writeValueAsString(registrationModel);

        MvcResult result = this.mockMvc.perform(
                post("/auth/register").
                        contentType(MediaType.APPLICATION_JSON).
                        content(json).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andExpect(header().exists("x-token")).
                andExpect(header().exists("Access-Control-Allow-Headers")).
                andExpect(header().string("Access-Control-Allow-Headers", "x-token")).
                andExpect(header().exists("Access-Control-Expose-Headers")).
                andExpect(header().string("Access-Control-Expose-Headers", "x-token")).
                andReturn();

        String token = result.getResponse().getHeader("x-token");
        this.mockMvc.perform(
                post("/auth/validate").
                        contentType(MediaType.APPLICATION_JSON).
                        header("Authorization", "Bearer " + token)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.email", is(registrationModel.getEmail()))).
                andExpect(jsonPath("$.firstName", is(registrationModel.getFirstName()))).
                andExpect(jsonPath("$.lastName", is(registrationModel.getLastName()))).
                andExpect(jsonPath("$.roles[0].role", is("ROLE_USER"))).
                andExpect(jsonPath("$.password", is(nullValue())));
    }

    @Test
    public void testCreateUserFailWithWrongRepeatPassword() throws Exception {

        RegistrationRequest registrationModel = createRegistrationModel();
        registrationModel.setRepeatPassword("wrong-repeat-password");

        String json = objectMapper.writeValueAsString(registrationModel);

        this.mockMvc.perform(
                post("/auth/register").
                        contentType(MediaType.APPLICATION_JSON).
                        content(json)).
                andExpect(status().isBadRequest());

    }

    @Test
    public void testCreateUserFailWithInvalidEmail() throws Exception {

        RegistrationRequest registrationModel = createRegistrationModel();
        registrationModel.setEmail("invalidem.email");

        String json = objectMapper.writeValueAsString(registrationModel);

        this.mockMvc.perform(
                post("/auth/register").
                        contentType(MediaType.APPLICATION_JSON).
                        content(json)).
                andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUserFailWithNotNullValidation() throws Exception {

        RegistrationRequest registrationModel = createRegistrationModel();
        registrationModel.setEmail(null);

        String json = objectMapper.writeValueAsString(registrationModel);

        this.mockMvc.perform(
                post("/auth/register").
                        contentType(MediaType.APPLICATION_JSON).
                        content(json)).
                andExpect(status().isBadRequest());
    }

    private RegistrationRequest createRegistrationModel(){

        final RegistrationRequest registrationModel = new RegistrationRequest();
        registrationModel.setEmail(NEW_USER_EMAIL);
        registrationModel.setFirstName(NEW_USER_FNAME);
        registrationModel.setLastName(NEW_USER_LNAME);
        registrationModel.setPassword(NEW_USER_PASSWORD);
        registrationModel.setRepeatPassword(NEW_USER_PASSWORD);

        return registrationModel;
    }
}
