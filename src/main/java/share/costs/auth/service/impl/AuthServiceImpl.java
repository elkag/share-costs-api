package share.costs.auth.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import share.costs.auth.oauth2.OAuth2UserInfo;
import share.costs.auth.oauth2.OAuth2UserInfoFactory;
import share.costs.auth.oauth2.SocialAuthProvider;
import share.costs.auth.service.AuthService;
import share.costs.auth.model.RegistrationRequest;
import share.costs.config.SocialLoginProperties;
import share.costs.config.security.PasswordEncoder;
import share.costs.exceptions.HttpBadRequestException;
import share.costs.users.converters.UserConverter;
import share.costs.users.entities.AuthorityEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;
import share.costs.users.model.UserModel;
import share.costs.users.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final UserDetailsService userDetailsService;
    private final SocialLoginProperties socialLoginProperties;
    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    public AuthServiceImpl(UserRepository userRepository, UserConverter userConverter, UserDetailsService userDetailsService, SocialLoginProperties socialLoginProperties, PasswordEncoder passwordEncoder, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.userDetailsService = userDetailsService;
        this.socialLoginProperties = socialLoginProperties;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    private UserModel getOrCreateFacebookUser(OAuth2UserInfo oAuth2UserInfo) {

        Optional<UserEntity> userEntityOpt =
                userRepository.findOneByEmail(oAuth2UserInfo.getEmail());

        final UserEntity user =  userEntityOpt.
                orElseGet(() -> createFacebookUser(oAuth2UserInfo.getEmail()));

        final UserEntity updated = updateUser(user, oAuth2UserInfo);

        return userConverter.convertToModel(updated);
    }

    private UserEntity updateUser(UserEntity userEntity, OAuth2UserInfo facebookInfo) {
        userEntity.setImage(facebookInfo.getImageUrl());
        userEntity.setFirstName(facebookInfo.getFirstName());
        userEntity.setLastName(facebookInfo.getLastName());

        return userRepository.save(userEntity);
    }

    private UserEntity createFacebookUser(String email) {
        LOGGER.info("Creating a new user with email [PROTECTED].");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        AuthorityEntity userRole = new AuthorityEntity().setRole("ROLE_USER");
        userEntity.setRoles(List.of(userRole));

        return userRepository.save(userEntity);
    }

    @Override
    public UserModel getUserByEmail(final String email) {
        final Optional<UserEntity> userOpt = userRepository
                .findOneByEmail(email);

        return userConverter.convertToModel(userOpt.orElse(null));
    }

    @Override
    public UserModel registerUser(final RegistrationRequest request) {
        LOGGER.info("Register user BEGIN: {}", request);
        final UserModel model = new UserModel();
        model.setEmail(request.getEmail());
        model.setFirstName(request.getFirstName());
        model.setLastName(request.getLastName());
        model.setPassword(request.getPassword());
        model.setRoles(List.of(new AuthorityEntity().setRole("USER")));
        model.setPassword(passwordEncoder.hashPassword(model.getPassword()));

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
    public UserModel validateAndLoginFacebookUser(String accessToken) {

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
            throw new HttpBadRequestException(exp.getMessage());
        }
    }

}
