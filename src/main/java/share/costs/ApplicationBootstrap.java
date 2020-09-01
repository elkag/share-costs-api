package share.costs;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import share.costs.config.security.PasswordEncoder;
import share.costs.users.entities.RoleEntity;
import share.costs.users.entities.UserEntity;
import share.costs.users.entities.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ApplicationBootstrap implements CommandLineRunner {

    private  final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        // pre-populating the app with some demo data.
        createUsers();
    }

    private void createUsers() {
        if(userRepository.count() == 0) {
            final UserEntity userEntity = new UserEntity();
            userEntity.setEmail("elka.ganeva@gmail.com");
            userEntity.setFirstName("Elka");
            userEntity.setLastName("Ganeva");
            userEntity.setPassword(PasswordEncoder.hashPassword("admin"));

            RoleEntity adminRole = new RoleEntity().setRole("ROLE_ADMIN");
            RoleEntity userRole = new RoleEntity().setRole("ROLE_USER");
            userEntity.setRoles(List.of(adminRole, userRole));

            userRepository.save(userEntity);
        }
    }
}
