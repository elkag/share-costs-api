package share.costs.auth.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import share.costs.users.entities.AuthorityEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    private UserDetailsServiceImpl service;

    @Mock
    private UserRepository mockUserRepository;

    private String TEST_USER_NAME_EXIST = "user@exist.com";
    private String TEST_USER_NAME_NOT_EXIST = "usernot@exist.com";

    private UserEntity testUserEntity;

    @BeforeEach
    public void setUp(){
        service = new UserDetailsServiceImpl(mockUserRepository);

        testUserEntity = new UserEntity();
        testUserEntity.setEmail("user@email");
        testUserEntity.setFirstName("userFirstName");
        testUserEntity.setLastName("userLastName");
        testUserEntity.setPassword("userPassword");
        AuthorityEntity user1Role = new AuthorityEntity().setRole("ROLE_USER").setRole("ROLE_ADMIN");
        testUserEntity.setRoles(List.of(user1Role));
    }

    @Test
    public void testUserNotFound(){
        when(mockUserRepository.findOneByEmail(TEST_USER_NAME_NOT_EXIST)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(TEST_USER_NAME_NOT_EXIST));
    }

    @Test
    public void testUsernameExist(){

        when(mockUserRepository.findOneByEmail(TEST_USER_NAME_EXIST)).thenReturn(Optional.of(testUserEntity));

        UserDetails actualDetails = service.loadUserByUsername(TEST_USER_NAME_EXIST);

        Assertions.assertEquals(testUserEntity.getEmail(), actualDetails.getUsername());
        Assertions.assertEquals(testUserEntity.getPassword(), actualDetails.getPassword());
        Assertions.assertEquals(testUserEntity.getRoles().size(), actualDetails.getAuthorities().size());

        //Assert.assertTrue(actualDetails.getAuthorities().containsAll(testUserEntity.getRoles()));
    }
}
