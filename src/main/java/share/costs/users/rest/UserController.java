package share.costs.users.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import share.costs.users.entities.UserEntity;
import share.costs.users.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  public UserController(UserService userService) {
    this.userService = userService;
  }


}
