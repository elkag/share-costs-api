package share.costs.groups.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import share.costs.users.entities.User;
import share.costs.users.model.GroupUserModel;
import share.costs.users.model.UserModel;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupModel {
    String id;
    String name;
    GroupUserModel owner;
    private Date date;
    private String status;
    private BigDecimal balance;
    private String description;
    List<GroupUserModel> users;
    List<GroupUserModel> pendingUsers;

}