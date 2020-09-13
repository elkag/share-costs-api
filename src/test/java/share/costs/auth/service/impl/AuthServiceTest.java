package share.costs.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.web.client.RestTemplate;
import share.costs.auth.model.RegistrationRequest;
import share.costs.config.SocialLoginProperties;
import share.costs.config.security.PasswordEncoder;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.users.converters.UserConverter;
import share.costs.users.entities.AuthorityEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;
import share.costs.users.model.UserModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private AuthServiceImpl authService;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private SocialLoginProperties mockSocialLoginProperties;

    @Mock
    private RestTemplate mockRestTemplate;

    private UserEntity existingUserEntity;

    private final String NON_EXISTING_EMAIL = "nonExisting@Email.com";
    private final String EXISTING_USER_EMAIL = "existing@Email.com";

    private final String EXISTING_USER_ID = "existing-user-id";


    @BeforeEach
    public void setUp(){

        authService = new AuthServiceImpl(
                mockUserRepository,
                new UserConverter(),
                new UserDetailsServiceImpl(mockUserRepository),
                mockSocialLoginProperties,
                new PasswordEncoder(), mockRestTemplate);

        existingUserEntity = new UserEntity();
        existingUserEntity.setId(EXISTING_USER_ID);
        existingUserEntity.setEmail(EXISTING_USER_EMAIL);
        existingUserEntity.setFirstName("userFirstName");
        existingUserEntity.setLastName("userLastName");
        existingUserEntity.setPassword("userPassword");
        AuthorityEntity userUserRole = new AuthorityEntity().setRole("ROLE_USER");
        AuthorityEntity userAdminRole = new AuthorityEntity().setRole("ROLE_ADMIN");
        existingUserEntity.setRoles(List.of(userUserRole, userAdminRole));
    }

    @Test
    public void testRegisterUserSuccess(){

        final String NEW_USER_ID = "new-user-id";
        final String NEW_USER_EMAIL = "newUser@Email.com";
        final String FIRST_NAME = "NewUserFirstName";
        final String LAST_NAME = "NewUserLastName";
        final String PASSWORD = "new-user-super-strong-password";

        when(mockUserRepository.findOneByEmail(NEW_USER_EMAIL)).thenReturn(Optional.empty());

        when(mockUserRepository.save(any())).thenAnswer(
                (Answer<UserEntity>) invocation -> {
                    UserEntity userToSave;
                    userToSave = invocation.getArgument(0);
                    userToSave.setId(NEW_USER_ID);
                    return userToSave;
                }
        );

        final RegistrationRequest registrationRequest = new RegistrationRequest().
                setEmail(NEW_USER_EMAIL).
                setFirstName(FIRST_NAME).
                setLastName(LAST_NAME).
                setPassword(PASSWORD).
                setRepeatPassword(PASSWORD);

        UserModel userModel = authService.registerUser(registrationRequest);

        Assertions.assertEquals(userModel.getEmail(), registrationRequest.getEmail());
        Assertions.assertEquals(NEW_USER_ID, userModel.getId());
        Assertions.assertNull(userModel.getPassword());

        ArgumentCaptor<UserEntity> argument = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.verify(mockUserRepository, times(1)).save(argument.capture());

        UserEntity userActual = argument.getValue();

        Assertions.assertEquals(userModel.getId(), userActual.getId());
        Assertions.assertEquals(userModel.getEmail(), registrationRequest.getEmail());
        Assertions.assertEquals(userModel.getFirstName(), registrationRequest.getFirstName());
        Assertions.assertEquals(userModel.getLastName(), registrationRequest.getLastName());
        Assertions.assertTrue(new PasswordEncoder().checkPassword(registrationRequest.getPassword(), userActual.getPassword()));
    }

    @Test
    public void testRegisterExistingUser() {

        final String NEW_USER_EMAIL = EXISTING_USER_EMAIL;
        final String FIRST_NAME = "NewUserFirstName";
        final String LAST_NAME = "NewUserLastName";
        final String PASSWORD = "new-user-super-strong-password";

        when(mockUserRepository.findOneByEmail(NEW_USER_EMAIL)).thenReturn(Optional.of(existingUserEntity));

        final RegistrationRequest registrationRequest = new RegistrationRequest().
                setEmail(NEW_USER_EMAIL).
                setFirstName(FIRST_NAME).
                setLastName(LAST_NAME).
                setPassword(PASSWORD).
                setRepeatPassword(PASSWORD);

        Assertions.assertThrows(HttpBadRequestException.class, () -> authService.registerUser(registrationRequest));
    }

    @Test
    public void testValidateEmail() {

        final String NEW_USER_EMAIL = "wrong-email";
        final String FIRST_NAME = "NewUserFirstName";
        final String LAST_NAME = "NewUserLastName";
        final String PASSWORD = "new-user-super-strong-password";

        final RegistrationRequest registrationRequest = new RegistrationRequest().
                setEmail(NEW_USER_EMAIL).
                setFirstName(FIRST_NAME).
                setLastName(LAST_NAME).
                setPassword(PASSWORD).
                setRepeatPassword(PASSWORD);

        Assertions.assertThrows(HttpBadRequestException.class, () -> authService.registerUser(registrationRequest));
    }

    @Test
    public void testGetFullAndCorrectUserEntityByEmail(){
        when(mockUserRepository.findOneByEmail(EXISTING_USER_EMAIL)).thenReturn(Optional.of(existingUserEntity));

        UserModel model = authService.getUserByEmail(EXISTING_USER_EMAIL);

        Assertions.assertEquals(existingUserEntity.getId(), model.getId());
        Assertions.assertEquals(existingUserEntity.getEmail(), model.getEmail());
        Assertions.assertEquals(existingUserEntity.getFirstName(), model.getFirstName());
        Assertions.assertEquals(existingUserEntity.getLastName(), model.getLastName());
        Assertions.assertNull(model.getPassword());
        Assertions.assertEquals(existingUserEntity.getRoles().size(), model.getRoles().size());
        Assertions.assertTrue(model.getRoles().contains(existingUserEntity.getRoles().get(0)));
        Assertions.assertTrue(model.getRoles().contains(existingUserEntity.getRoles().get(1)));
    }

    @Test
    public void testNullUserEntityByEmail(){
        UserModel model = authService.getUserByEmail(EXISTING_USER_EMAIL);

        Assertions.assertNull(model);
    }

    @Test
    public void testValidateAndLogFacebookUser() throws JsonProcessingException {

        final String fbUserEmail = "fbUserEmail@com";
        final String fbUserFirstName = "fbUserFirstName";
        final String fbUserLastName = "fbUserLastName";
        final String fbUserImg = "fbUser_imageUrl";

        when(mockUserRepository.findOneByEmail((fbUserEmail))).thenReturn(Optional.empty());

        UserEntity userToSave = new UserEntity();
        userToSave.setEmail(fbUserEmail);

        when(mockUserRepository.save(any())).thenAnswer((Answer<UserEntity>) invocation -> {
            userToSave.setId("fb-user-id");
            return userToSave;
        });

        String json = "{\"email\": \"" + fbUserEmail + "\", " +
                        "\"first_name\": \""  + fbUserFirstName + "\", " +
                        "\"last_name\": \"" + fbUserLastName + "\", " +
                        "\"id\": \"123\"," +
                        "\"data\": {" +
                        "\"url\":\"" + fbUserImg + "\"}}";

        Map<String, Object> map = new ObjectMapper().readValue(json, Map.class);

        when(mockSocialLoginProperties.getUserInfoUri()).thenReturn("http://uri.com");

        when(mockRestTemplate.getForObject(any(String.class), any())).thenReturn(map);

        UserModel saved = authService.validateAndLoginFacebookUser("FB_TOKEN");

        Assertions.assertEquals(fbUserEmail, saved.getEmail());
        Assertions.assertEquals(fbUserFirstName, saved.getFirstName());
        Assertions.assertEquals(fbUserLastName, saved.getLastName());
    }
}
