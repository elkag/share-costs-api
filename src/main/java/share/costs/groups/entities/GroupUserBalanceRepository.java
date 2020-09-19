package share.costs.groups.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import share.costs.users.entities.UserEntity;

import java.util.Optional;

public interface GroupUserBalanceRepository extends JpaRepository<GroupUserBalance, Long> {
    Optional<GroupUserBalance> findByUserAndGroup(UserEntity user, Group group);
}
