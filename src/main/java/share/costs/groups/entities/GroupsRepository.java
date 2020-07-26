package share.costs.groups.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import share.costs.users.entities.User;

import java.util.List;

@Repository
public interface GroupsRepository extends JpaRepository<Group, String> {
    List<Group> findAllByOwner(User user);
    //List<Group> findAllByUser();
    List<Group> findAll();
}
