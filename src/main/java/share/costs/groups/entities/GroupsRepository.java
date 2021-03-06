package share.costs.groups.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupsRepository extends JpaRepository<Group, Long> {

    Optional<Group> findById(String id);

    List<Group> findGroupsByUsersId(Long userId);

    List<Group> findGroupsByPendingUsersUserId(Long userId);
}
