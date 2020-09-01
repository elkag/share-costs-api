package share.costs.config.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import share.costs.config.security.jwt.JWTAuthorizationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import share.costs.users.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import share.costs.users.oauth2.OAuth2AuthenticationFailureHandler;
import share.costs.users.oauth2.OAuth2AuthenticationSuccessHandler;
import share.costs.users.oauth2.services.CustomOAuth2UserServiceImpl;
import share.costs.users.auth.RestAuthenticationEntryPoint;

import static share.costs.config.security.SecurityConstants.STATS_URL;
import static share.costs.config.security.SecurityConstants.USERS_URL;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final OAuth2AuthenticationSuccessHandler successHandler;
  private final OAuth2AuthenticationFailureHandler failureHandler;
  private final CustomOAuth2UserServiceImpl oAuth2UserService;

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  /*
     By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
     the authorization request. But, since our service is stateless, we can't save it in
     the session. We'll save the request in a Base64 encoded cookie instead.
   */
  @Bean
  public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .cors()
        .and()
        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()
            .exceptionHandling()
              .authenticationEntryPoint(new RestAuthenticationEntryPoint())
              .and()
        .authorizeRequests()
            .antMatchers(HttpMethod.POST, USERS_URL)
              .permitAll()
            .antMatchers(HttpMethod.GET, STATS_URL)
              .permitAll()
            .antMatchers("/auth/**", "/oauth2/**")
              .permitAll()
            .anyRequest().authenticated()
            .and()
        .oauth2Login()
            .authorizationEndpoint()
            .baseUri("/oauth2/authorize")
            .authorizationRequestRepository(cookieAuthorizationRequestRepository())
            .and()
            .redirectionEndpoint().baseUri("/oauth2/callback/*")
            .and()
            .userInfoEndpoint().userService(oAuth2UserService)
            .and()
            .successHandler(successHandler)
            .failureHandler(failureHandler);

    http.addFilter(new JWTAuthorizationFilter(authenticationManager()));
  }

  

  @Override
  public void configure(final WebSecurity web) {
    web.ignoring().antMatchers(
        "/users/**",
        "/configuration/ui",
        "/configuration/**",
        "/actuator/**",
        "/v2/api-docs",
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/webjars/**");
  }

}
