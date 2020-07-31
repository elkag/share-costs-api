package share.costs.balances.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalancesRepository extends JpaRepository<Balance, String> {
    List<Optional<Balance>> findByIdIn(List<String> id);
}
