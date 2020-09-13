package share.costs.auth.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import share.costs.auth.service.AuthService;
import share.costs.config.security.SecurityConstants;
import share.costs.users.model.UserModel;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerFBLoginTest {

    @MockBean
    private AuthService mockAuthService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLoginFacebookUserFail() throws Exception {

        this.mockMvc.perform(
                post("/auth/facebook-login")).
                andExpect(status().isUnauthorized());

    }

    @Test
    public void testLoginFacebookUserSuccess() throws Exception {

        // Arrange
        final String token = "FB-token";

        final UserModel userModel = new UserModel();
        userModel.setEmail("fbuser@email.com");

        when(mockAuthService.validateAndLoginFacebookUser(token)).thenReturn(userModel);

        this.mockMvc.perform(
                post("/auth/facebook-login").
                        header(SecurityConstants.HEADER_STRING, token)).
                andExpect(status().isOk()).
                andExpect(header().exists("x-token")).
                andExpect(header().exists("Access-Control-Allow-Headers")).
                andExpect(header().string("Access-Control-Allow-Headers", "x-token")).
                andExpect(header().exists("Access-Control-Expose-Headers")).
                andExpect(header().string("Access-Control-Expose-Headers", "x-token"));
    }


}
