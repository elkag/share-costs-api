package share.costs.groups.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import share.costs.users.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupsRepository extends JpaRepository<Group, String> {

    Optional<Group> findById(String id);

    List<Group> findGroupsByUsersId(String userId);

    List<Group> findGroupsByPendingUsersUserId(String userId);
}
