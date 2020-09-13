package share.costs.groups.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequest {

    String groupId;

    String userId;

    public String getGroupId() {
        return groupId;
    }

    public AddUserRequest setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public AddUserRequest setUserId(String userId) {
        this.userId = userId;
        return this;
    }
}
