package share.costs.users.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.exceptions.HttpUnauthorizedException;
import share.costs.users.auth.UserDetailsServiceImpl;
import share.costs.users.entities.RoleEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;
import share.costs.users.model.UserModel;
import org.springframework.stereotype.Service;
import share.costs.config.security.PasswordEncoder;
import share.costs.users.oauth2.userInfo.OAuth2UserInfo;
import share.costs.users.auth.UserDetailsService;
import share.costs.users.service.converters.UserConverter;
import share.costs.users.service.UserService;

import javax.validation.*;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final UserDetailsService userDetailsService;

  public UserServiceImpl(final UserRepository userRepository,
                         final UserConverter userConverter, UserDetailsServiceImpl userDetailsService) {
    this.userRepository = userRepository;
    this.userConverter = userConverter;
      this.userDetailsService = userDetailsService;
  }


    public UserModel getOrCreateUser(String email) {

        Optional<UserEntity> userEntityOpt =
                userRepository.findOneByEmail(email);

        final UserEntity user =  userEntityOpt.
                orElseGet(() -> createUser(email));

        return userConverter.convertToModel(user);
    }

    public UserModel getOrCreateUser(OAuth2UserInfo oAuth2UserInfo) {

        Optional<UserEntity> userEntityOpt =
                userRepository.findOneByEmail(oAuth2UserInfo.getEmail());

        final UserEntity user =  userEntityOpt.
                orElseGet(() -> createUser(oAuth2UserInfo));

        return userConverter.convertToModel(user);
    }



    public void registerAndLoginUser(String userEmail, String userPassword) {
        UserEntity userEntity = createUser(userEmail, userPassword);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getEmail());

        Authentication authentication = new
                UsernamePasswordAuthenticationToken(
                userDetails,
                userEntity.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserEntity createUser(String email) {
        return this.createUser(email, null);
    }

    private UserEntity createUser(String userEmail, String userPassword) {
        LOGGER.info("Creating a new user with email [PROTECTED].");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userEmail);
        if (userPassword != null) {
            userEntity.setPassword(PasswordEncoder.hashPassword(userPassword));
        }

        RoleEntity userRole = new RoleEntity().setRole("ROLE_USER");
        userEntity.setRoles(List.of(userRole));

        return userRepository.save(userEntity);
    }

    private UserEntity createUser(OAuth2UserInfo oAuth2UserInfo) {
        LOGGER.info("Creating a new user with email [PROTECTED].");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(oAuth2UserInfo.getEmail());
        userEntity.setImage(oAuth2UserInfo.getImageUrl());

        RoleEntity userRole = new RoleEntity().setRole("ROLE_USER");
        userEntity.setRoles(List.of(userRole));

        return userRepository.save(userEntity);
    }

  @Override
  public UserModel registerUser(final UserModel model) {
        LOGGER.info("Register user BEGIN: {}", model);

        model.setPassword(PasswordEncoder.hashPassword(model.getPassword()));

        final UserEntity user = userConverter.convertToEntity(model);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        if(!violations.isEmpty()) {
          throw new HttpBadRequestException(violations.stream().findFirst().get().getMessage());
        }

        final Optional<UserEntity> userOpt = userRepository
              .findOneByEmail(user.getEmail());

        if(userOpt.isPresent()) {
          throw new HttpBadRequestException(String.format("Email {} already exists", user.getEmail()));
        };
        final UserEntity saved = userRepository.save(user);

        LOGGER.info("Register user END: {}", saved);

        return userConverter.convertToModel(saved);
  }

    public UserModel getUserInfoByEmail(String email) {
      UserEntity user = getUser(email);

      if(user != null) {
          return userConverter.convertToModel(user);
      }

      throw new HttpUnauthorizedException("Server error");
    }

    private UserEntity getUser(final String email) {
        final Optional<UserEntity> userOpt = userRepository
            .findOneByEmail(email);

        return userOpt.orElse(null);
      }
}
