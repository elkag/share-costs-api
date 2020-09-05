package share.costs.users.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import share.costs.config.security.SecurityConstants;
import share.costs.config.security.TokenProvider;
import share.costs.users.model.LoginModel;
import share.costs.users.model.RegistrationModel;
import share.costs.users.model.UserModel;
import share.costs.users.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final TokenProvider tokenProvider;

  private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  private final AuthenticationManager authenticationManager;

  public UserController(UserService userService, TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.tokenProvider = tokenProvider;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/register")
  public void registerUser(@Valid @RequestBody final RegistrationModel registrationModel, HttpServletResponse response) {

    final UserModel userModel = new UserModel();
    userModel.setEmail(registrationModel.getEmail());
    userModel.setFirstName(registrationModel.getFirstName());
    userModel.setLastName(registrationModel.getLastName());
    userModel.setPassword(registrationModel.getPassword());

    final UserModel registered = userService.registerUser(userModel);

    final String jwtToken = tokenProvider.createToken(registered.getEmail());

    response.addHeader("Access-Control-Allow-Headers", "x-token");
    response.addHeader("Access-Control-Expose-Headers", "x-token");
    response.addHeader("x-token", jwtToken);
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
  public void loginFacebookUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final String token = request.getHeader(SecurityConstants.HEADER_STRING);

    if (token == null) {
      response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid authentication!");
      return;
    }

    // Call facebook's API to validate id_token and get user's details
    final UserModel userModel = userService.validateAndLogFacebookUser(token);

    final String jwtToken = tokenProvider.createToken(userModel.getEmail());

    response.addHeader("Access-Control-Allow-Headers", "x-token");
    response.addHeader("Access-Control-Expose-Headers", "x-token");
    response.addHeader("x-token", jwtToken);
  }
}
