package share.costs.users.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PendingUserRepository extends JpaRepository<PendingUser, String> {
    List<PendingUser> findByUserId(String id);
}
