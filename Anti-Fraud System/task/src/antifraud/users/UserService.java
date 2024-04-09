package antifraud.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (repository.findAll().isEmpty()) {
            user.setAuthority("ROLE_ADMINISTRATOR");
            user.isLocked(false);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUsername(user.getUsername());
        return repository.save(user);
    }

    @Transactional
    public void delete(String username) {
        if (repository.existsByUsername(username)) {
            repository.deleteUserByUsername(username);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<User> returnAllUsers() {
        return repository.findByOrderById();
    }

    public User assignRole(String username, String role) {
        if (Arrays.stream(Roles.values()).noneMatch(validRole -> validRole.name().equals(role))
                || role.equals("ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (repository.existsByUsername(username)) {
            User user = repository.findUserByUsername(username);
            if (user.getRole().equals(role)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
            user.setAuthority("ROLE_" + role);
            return repository.save(user);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public void activateUser(String username, String operation) {
        if (repository.existsByUsername(username)) {
            User user = repository.findUserByUsername(username);
            if (operation.equals("LOCK")) {
                if (user.getRole().equals("ADMINISTRATOR")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
                user.isLocked(true);
            } else if (operation.equals("UNLOCK")) {
                user.isLocked(false);
            }
            repository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository
                .findUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
    }
}
