package share.costs.users.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import share.costs.groups.entities.Group;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

  Boolean existsUserByUsername(String username);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String username);

  Optional<User> findById(String id);

  List<Optional<User>> findByUsernameIgnoreCaseStartsWith(String username);

  List<Optional<User>> findByFirstNameIgnoreCaseStartsWith(String firstName);

  List<Optional<User>> findByLastNameIgnoreCaseStartsWith(String lastName);

  List<Optional<User>> findByEmailIgnoreCaseContains(String email);

}

