package share.costs.groups.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequest {

    Long groupId;

    Long userId;

    public Long getGroupId() {
        return groupId;
    }

    public AddUserRequest setGroupId(Long groupId) {
        this.groupId = groupId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AddUserRequest setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
