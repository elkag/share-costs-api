package share.costs.users.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsernameCheckResponse {

    private String username;

    private Boolean error;

    private String message;

}
