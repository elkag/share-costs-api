package share.costs.users.service.impl;

import org.springframework.transaction.annotation.Transactional;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.users.entities.User;
import share.costs.users.entities.UserRepository;
import share.costs.users.model.UserModel;
import share.costs.users.rest.LoginResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import share.costs.config.security.PasswordEncoder;
import share.costs.config.security.SecurityConstants;
import share.costs.users.rest.UsernameCheckResponse;
import share.costs.users.service.converters.UserConverter;
import share.costs.users.service.UserService;

import javax.validation.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserConverter userConverter;

  public UserServiceImpl(final UserRepository userRepository,
      final UserConverter userConverter) {
    this.userRepository = userRepository;
    this.userConverter = userConverter;
  }

  @Override
  public UserModel registerUser(final UserModel model) {
    log.info("Register user BEGIN: {}", model);

        model.setPassword(PasswordEncoder.hashPassword(model.getPassword()));

        final User user = userConverter.convertToEntity(model);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if(!violations.isEmpty()) {
          throw new HttpBadRequestException(violations.stream().findFirst().get().getMessage());
        }

        final Optional<User> userOpt = userRepository
              .findByUsername(user.getUsername());

        if(userOpt.isPresent()) {
          throw new HttpBadRequestException(String.format("Username '%s' already exists", user.getUsername()));
        };
        final User saved = userRepository.save(user);

        log.info("Register user END: {}", saved);

        final UserModel created = userConverter.convertToModel(saved);

        return created;
  }

  @Override
  public LoginResponse loginUser(final String username, final String password) {
    log.info("Login user BEGIN: {}", username);

    final User user = getUser(username);

    if(user == null) {
      throw new HttpBadRequestException("Wrong username");
    }
    if (!PasswordEncoder.checkPassword(password, user.getPassword())) {
      throw new HttpBadRequestException("Wrong password");
    }

    final String jwtToken = JWT.create()
        .withSubject(username)
        .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
        .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

    final UserModel userModel = userConverter.convertToModel(user);

    final LoginResponse response = new LoginResponse(userModel, jwtToken);

    log.info("Login user END: {}", response);

    return response;
  }

    @Override
    public UsernameCheckResponse checkUsername(String username) {
        final Optional<User> userOpt = userRepository
                .findByUsername(username);

        if(userOpt.isPresent()) {
            User user = userOpt.get();
            return new UsernameCheckResponse(user.getUsername(), true, String.format("'%s' is already taken", user.getUsername()));
        };

        return new UsernameCheckResponse(username, false, "");

    }

    @Override
    public void joinGroup(String groupId) {

    }

    private User getUser(final String username) {
        final Optional<User> userOpt = userRepository
            .findByUsername(username);

        return userOpt.orElse(null);
      }

}
