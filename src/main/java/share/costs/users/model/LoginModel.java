package share.costs.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginModel {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}
