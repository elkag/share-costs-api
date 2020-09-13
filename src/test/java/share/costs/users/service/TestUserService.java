package share.costs.users.service;

import share.costs.auth.service.AuthService;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.users.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class TestUserService {
/*
  @Autowired
  private AuthService userService;

  @Test
  public void testRegisterUser() {
    final UserModel model = new UserModel("", "123", "Petko", "Petkov", "petko@petko.com", new ArrayList<>());

    final UserModel created = userService.registerUser(model);

    assertEquals(model.getFirstName(), created.getFirstName());
    assertEquals(model.getLastName(), created.getLastName());
  }

  @Test
  public void testEmailValidation() {
    final UserModel model = new UserModel("", "123", "Petko", "Petkov", "@etko.d", new ArrayList<>());

    assertThrows(
            HttpBadRequestException.class,
            () -> userService.registerUser(model));
  }*/
}