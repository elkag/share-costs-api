package share.costs.users.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import share.costs.auth.service.UserDetailsServiceImpl;
import share.costs.config.SocialLoginProperties;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.auth.oauth2.OAuth2UserInfo;
import share.costs.auth.oauth2.OAuth2UserInfoFactory;
import share.costs.auth.oauth2.SocialAuthProvider;
import share.costs.users.entities.AuthorityEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;
import share.costs.users.model.UserModel;
import org.springframework.stereotype.Service;
import share.costs.config.security.PasswordEncoder;
import share.costs.users.service.converters.UserConverter;
import share.costs.users.service.UserService;

import javax.validation.*;
import java.util.*;

@SuppressWarnings("unchecked")
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final UserDetailsService userDetailsService;
    private final SocialLoginProperties socialLoginProperties;

    public UserServiceImpl(final UserRepository userRepository,
                           final UserConverter userConverter, UserDetailsServiceImpl userDetailsService, SocialLoginProperties socialLoginProperties) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.userDetailsService = userDetailsService;
        this.socialLoginProperties = socialLoginProperties;
    }


    public UserModel getOrCreateUser(String email) {

        Optional<UserEntity> userEntityOpt =
                userRepository.findOneByEmail(email);

        final UserEntity user =  userEntityOpt.
                orElseGet(() -> createUser(email));

        return userConverter.convertToModel(user);
    }

    public UserModel getOrCreateFacebookUser(OAuth2UserInfo oAuth2UserInfo) {

        Optional<UserEntity> userEntityOpt =
                userRepository.findOneByEmail(oAuth2UserInfo.getEmail());

        final UserEntity user =  userEntityOpt.
                orElseGet(() -> createUser(oAuth2UserInfo.getEmail()));

        final UserEntity updated = updateUser(user, oAuth2UserInfo);

        return userConverter.convertToModel(updated);
    }

    private UserEntity updateUser(UserEntity userEntity, OAuth2UserInfo facebookInfo) {
        userEntity.setImage(facebookInfo.getImageUrl());
        userEntity.setFirstName(facebookInfo.getFirstName());
        userEntity.setLastName(facebookInfo.getLastName());

        return userRepository.save(userEntity);
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

        AuthorityEntity userRole = new AuthorityEntity().setRole("ROLE_USER");
        userEntity.setRoles(List.of(userRole));

        return userRepository.save(userEntity);
    }

  @Override
  public UserModel registerUser(final UserModel model) {
        LOGGER.info("Register user BEGIN: {}", model);

        model.setPassword(PasswordEncoder.hashPassword(model.getPassword()));

        final UserEntity user = userConverter.convertToEntity(model);

        AuthorityEntity userRole = new AuthorityEntity().setRole("ROLE_USER");
        user.setRoles(List.of(userRole));

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

    @Override
    public UserModel validateAndLogFacebookUser(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> attributes = null;

        final String fields = "id,email,first_name,last_name,picture";
        try {

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(socialLoginProperties.getUserInfoUri())
                .queryParam("access_token", accessToken).queryParam("fields", fields);

            attributes = restTemplate.getForObject(uriBuilder.toUriString(), Map.class);

            OAuth2UserInfo oAuth2UserInfo =
                    OAuth2UserInfoFactory.getOAuth2UserInfo(SocialAuthProvider.FACEBOOK, attributes);

            return getOrCreateFacebookUser(oAuth2UserInfo);

        } catch (HttpClientErrorException e) {
            throw new HttpBadRequestException("Invalid access token");
        } catch (Exception exp) {
            throw new HttpBadRequestException("Invalid user");
        }
    }

    @Override
    public UserEntity getUser(final String email) {
        final Optional<UserEntity> userOpt = userRepository
            .findOneByEmail(email);

        return userOpt.orElse(null);
      }
}
