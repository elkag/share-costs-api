package share.costs.users.service;

import share.costs.exceptions.HttpBadRequestException;
import share.costs.exceptions.HttpUnauthorizedException;
import share.costs.users.model.UserModel;
import share.costs.users.rest.LoginResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TestUserService {

  @Autowired
  private UserService userService;

  @Test
  public void testRegisterUser() {
    final UserModel model = new UserModel("", "petko", "123", "Petko", "Petkov", "petko@petko.com");

    final UserModel created = userService.registerUser(model);

    assertEquals(model.getUsername(), created.getUsername());
    assertEquals(model.getFirstName(), created.getFirstName());
    assertEquals(model.getLastName(), created.getLastName());
  }

  @Test
  public void testLoginUser() {
    final UserModel model = new UserModel("", "kiril", "123", "Kiril", "Kirilov", "kiril@kiril.com");

    final UserModel created = userService.registerUser(model);

    assertThrows(
            HttpBadRequestException.class,
        () -> userService.loginUser(created.getUsername(), "root"));

    final LoginResponse petkoLogin = userService.loginUser(created.getUsername(), "123");
    assertNotNull(petkoLogin.getUser());
    assertNotNull(petkoLogin.getJwtToken());
  }

  @Test
  public void testEmailValidation() {
    final UserModel model = new UserModel("", "ivan", "123", "Petko", "Petkov", "@etko.d");

    assertThrows(
            HttpBadRequestException.class,
            () -> userService.registerUser(model));
  }
}