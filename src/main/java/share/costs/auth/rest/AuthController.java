package share.costs.auth.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import share.costs.config.security.SecurityConstants;
import share.costs.config.security.TokenProvider;
import share.costs.auth.model.LoginModel;
import share.costs.auth.model.RegistrationRequest;
import share.costs.users.model.UserModel;
import share.costs.users.rest.UserController;
import share.costs.auth.service.AuthService;
import share.costs.users.converters.UserConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final AuthService authService;
    private final UserConverter userConverter;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, UserConverter userConverter, TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.userConverter = userConverter;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody final RegistrationRequest registrationRequest) {

        final UserModel registered = authService.registerUser(registrationRequest);

        final String jwtToken = tokenProvider.createToken(registered.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Headers", "x-token");
        headers.set("Access-Control-Expose-Headers", "x-token");
        headers.set("x-token", jwtToken);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public void loginUser(@Valid @RequestBody final LoginModel loginModel, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginModel.getEmail(), loginModel.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwtToken = tokenProvider.createToken(loginModel.getEmail());

        response.addHeader("Access-Control-Allow-Headers", "x-token");
        response.addHeader("Access-Control-Expose-Headers", "x-token");
        response.addHeader("x-token", jwtToken);
    }

    @PostMapping("/facebook-login")
    public ResponseEntity<Void> loginFacebookUser(HttpServletRequest request) {
        final String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Call facebook's API to validate id_token and get user's details
        final UserModel userModel = authService.validateAndLoginFacebookUser(token);

        final String jwtToken = tokenProvider.createToken(userModel.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Headers", "x-token");
        headers.set("Access-Control-Expose-Headers", "x-token");
        headers.set("x-token", jwtToken);

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PostMapping("/validate")
    public UserModel validateUser(@AuthenticationPrincipal Principal principal) {
        return authService.getUserByEmail(principal.getName());
    }
}
