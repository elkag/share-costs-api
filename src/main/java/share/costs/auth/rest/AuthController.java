package share.costs.auth.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import share.costs.users.model.UserModel;
import share.costs.users.service.UserService;
import share.costs.users.service.converters.UserConverter;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserConverter userConverter;

    public AuthController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @PostMapping("/validate")
    public UserModel validateUser(Principal principal) {
        return userConverter.convertToModel(userService.getUser(principal.getName()));
    }
}
