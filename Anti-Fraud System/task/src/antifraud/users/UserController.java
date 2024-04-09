package antifraud.users;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResult authenticate(@Valid @RequestBody RegistrationRequest request) {
        User user = new User(request.name(), request.username(), request.password());
        return new UserResult(service.register(user));
    }

    @DeleteMapping("user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public DeletedUserResult deleteUser(@PathVariable String username) {
        service.delete(username);
        return new DeletedUserResult(username);
    }

    @GetMapping("list")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResult> returnUsers() {
        return service.returnAllUsers().stream().map(UserResult::new).collect(Collectors.toList());
    }

    @PutMapping("role")
    @ResponseStatus(HttpStatus.OK)
    public UserResult assignRole(@Valid @RequestBody AssignRoleRequest request) {
        return new UserResult(service.assignRole(request.username, request.role));
    }

    @PutMapping("access")
    @ResponseStatus(HttpStatus.OK)
    public Status activateUser(@Valid @RequestBody ActivateUserRequest request) {
        service.activateUser(request.username, request.operation);
        return new Status(request.username, request.operation);
    }

    public record RegistrationRequest(@NotEmpty String name, @NotEmpty String username, @NotEmpty String password) {
    }

    public record AssignRoleRequest(@NotEmpty String username, @NotEmpty String role) {
    }

    public record ActivateUserRequest(@NotEmpty String username, String operation) {
    }

    public record Status(String status) {
        Status(String username, String operation) {
            this("User " + username + " " + operation.toLowerCase() + "ed!");
        }
    }

    public record UserResult(@NotEmpty long id, @NotEmpty String name, @NotEmpty String username,
                             @NotEmpty String role) {
        UserResult(User user) {
            this(user.getId(), user.getName(), user.getUsername(), user.getRole());
        }
    }

    public record DeletedUserResult(String username, String status) {
        DeletedUserResult(String username) {
            this(username, "Deleted successfully!");
        }
    }
}