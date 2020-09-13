package share.costs.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldMatch(
        first = "password",
        second = "repeatPassword",
        message = "Password do not match")
public class RegistrationRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String password;

    @NotNull
    private String repeatPassword;

    public RegistrationRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public RegistrationRequest setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public RegistrationRequest setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public RegistrationRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public RegistrationRequest setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
        return this;
    }
}
