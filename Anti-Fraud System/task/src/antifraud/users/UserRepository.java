package antifraud.users;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    User findUserByUsername(String username);
    List<User> findByOrderById();
    void deleteUserByUsername(String username);
    Optional<User> findUserByUsernameIgnoreCase(String username);
}