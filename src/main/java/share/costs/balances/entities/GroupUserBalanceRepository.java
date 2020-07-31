package share.costs.balances.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import share.costs.groups.entities.Group;
import share.costs.users.entities.User;

import java.util.Optional;

public interface GroupUserBalanceRepository extends JpaRepository<GroupUserBalance, String> {
    Optional<GroupUserBalance> findByUserAndGroup(User user, Group group);
}
