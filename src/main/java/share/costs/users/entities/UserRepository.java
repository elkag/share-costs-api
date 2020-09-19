package share.costs.users.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findOneByEmail(String username);

  Optional<UserEntity> findById(String id);

  List<Optional<UserEntity>> findByFirstNameIgnoreCaseStartsWith(String firstName);

  List<Optional<UserEntity>> findByLastNameIgnoreCaseStartsWith(String lastName);

  List<Optional<UserEntity>> findByEmailIgnoreCaseContains(String email);

}

