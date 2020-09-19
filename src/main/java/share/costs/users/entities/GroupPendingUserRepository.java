package share.costs.users.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupPendingUserRepository extends JpaRepository<GroupPendingUser, Long> {
}
