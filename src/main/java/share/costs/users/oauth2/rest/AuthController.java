package share.costs.users.oauth2.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import share.costs.config.security.SecurityConstants;
import share.costs.users.model.UserModel;
import share.costs.users.rest.LoginResponse;
import share.costs.users.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/authorize/facebook")
    public ResponseEntity socialLoginUser(String redirectUri) {

        return null;
    }

    @PostMapping("/current")
    public ResponseEntity getCurrentUser(@RequestHeader("authorization") String tokenStr) {
        tokenStr.replace(SecurityConstants.TOKEN_PREFIX, "");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        final UserModel model = userService.getUserInfoByEmail(username);

        final LoginResponse loginResponse = new LoginResponse();

        loginResponse.setJwtToken(tokenStr);
        loginResponse.setUser(model);
        return ResponseEntity.ok(loginResponse);
    }
}
