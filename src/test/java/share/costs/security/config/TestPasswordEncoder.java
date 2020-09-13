package share.costs.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import share.costs.config.security.PasswordEncoder;


@SpringBootTest
@ActiveProfiles("test")
public class TestPasswordEncoder {

  @Autowired
  private final PasswordEncoder passwordEncoder;

  public TestPasswordEncoder() {
    this.passwordEncoder = new PasswordEncoder();
  }


  @Test
  public void testPasswordEncoder() {
    String password = "abcdefghijklmnopqrstuvwxyz";
    String hash = "$2a$06$.rCVZVOThsIa97pEDOxvGuRRgzG64bvtJ0938xuqzv18d3ZpQhstC";

    System.out.println("Testing BCrypt Password hashing and verification");
    System.out.println("Test password: " + password);
    System.out.println("Test stored hash: " + hash);
    System.out.println("Hashing test password...");
    System.out.println();

    String computedHash = passwordEncoder.hashPassword(password);
    System.out.println("Test computed hash: " + computedHash);
    System.out.println();
    System.out.println("Verifying that hash and stored hash both match for the test password...");
    System.out.println();

    String compareTest = passwordEncoder.checkPassword(password, hash)
        ? "Passwords Match" : "Passwords do not match";
    String compareComputed = passwordEncoder.checkPassword(password, computedHash)
        ? "Passwords Match" : "Passwords do not match";

    System.out.println("Verify against stored hash:   " + compareTest);
    System.out.println("Verify against computed hash: " + compareComputed);
  }
}
