package share.costs.users.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.exceptions.HttpUnauthorizedException;
import share.costs.users.model.UserModel;
import share.costs.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public void registerUser(@RequestBody final UserModel user) {
      userService.registerUser(user);
  }

  @PostMapping("/login")
  public LoginResponse loginUser(@RequestBody final LoginRequest request) {
    return userService.loginUser(request.getUsername(), request.getPassword());
  }

  @PostMapping("/check-username")
  public UsernameCheckResponse checkUsername(@RequestBody final UsernameCheckRequest request) throws HttpBadRequestException {
    return userService.checkUsername(request.getUsername());
  }
}
