package share.costs.users.rest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import share.costs.config.security.SecurityConstants;
import share.costs.users.model.UserModel;
import share.costs.users.auth.UserDetailsService;
import share.costs.users.service.UserService;
import share.costs.users.auth.UserDetailsServiceImpl;

import java.util.Date;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserDetailsService userDetailsService;
  private final UserService userService;

  @Autowired
  private AuthenticationManager authenticationManager;

  public UserController(final UserDetailsServiceImpl userService, UserService userService1) {
    this.userDetailsService = userService;
    this.userService = userService1;
  }

  @PostMapping("/register")
  public void registerUser(@RequestBody final UserModel user) {
    userService.registerUser(user);
  }

  @PostMapping("/login")
  public ResponseEntity loginUser(@RequestBody final LoginRequest request) throws Exception {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));;

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserModel user = userService.getOrCreateUser(authentication.getName());

    final String jwtToken = JWT.create()
            .withSubject(authentication.getName())
            .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

    final LoginResponse loginResponse = new LoginResponse();
    loginResponse.setJwtToken(jwtToken);
    loginResponse.setUser(user);
    return ResponseEntity.ok(loginResponse);
  }

}
