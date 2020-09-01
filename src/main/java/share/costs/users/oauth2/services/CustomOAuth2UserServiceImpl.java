package share.costs.users.oauth2.services;

import lombok.Data;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import share.costs.users.UserPrincipal;
import share.costs.users.model.UserModel;
import share.costs.users.oauth2.userInfo.FacebookOAuth2UserInfo;
import share.costs.users.oauth2.userInfo.OAuth2UserInfo;
import share.costs.users.service.UserService;


@Service
@Data
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        OAuth2UserInfo oAuth2UserInfo = new FacebookOAuth2UserInfo(oAuth2User.getAttributes());

        return processOAuth2User(oAuth2User);
    }


    private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = new FacebookOAuth2UserInfo(oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            //throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        UserModel user =  userService.getOrCreateUser(oAuth2UserInfo);
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
}
