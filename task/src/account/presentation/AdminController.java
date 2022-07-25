package account.presentation;

import account.business.Log;
import account.business.LogService;
import account.business.User;
import account.business.UserService;
import account.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
public class AdminController {
    //    private User user;
    @Autowired
    UserService userService;

    @Autowired
    LogService logService;


    @PutMapping("/user/role")
    public UserDTO changeRole(@RequestBody Map<String, String> input,
                              @AuthenticationPrincipal UserDetailsImpl admin) {
        User user1 = userService.findUserByEmail(input.get("user"));
        String role = input.get("role");
        String operation = input.get("operation");

        if (user1 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        switch (operation) {
            case "GRANT":
                userService.grantRole(user1, role);
                logService.saveLog(new Log(
                        "GRANT_ROLE",
                        admin.getUsername().toLowerCase(),
                        String.format("Grant role %s to %s", role, user1.getEmail().toLowerCase()),
                        "api/admin/user"));
                break;
            case "REMOVE":
                userService.deleteRole(user1, role);
                logService.saveLog(new Log(
                        "REMOVE_ROLE",
                        admin.getUsername().toLowerCase(),
                        String.format("Remove role %s from %s", role, user1.getEmail().toLowerCase()),
                        "api/admin/user"));
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation must be only GRANT or REMOVE!");
        }
        return new UserDTO(user1);
    }

    @DeleteMapping("/user/{username}")
    @Transactional
    public Map<String, String> deleteUser(@AuthenticationPrincipal UserDetailsImpl admin, @PathVariable("username") String username) {
        User user1 = userService.findUserByEmail(username);
        if (user1 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        logService.saveLog(new Log(
                "DELETE_USER",
                admin.getUsername().toLowerCase(),
                username.toLowerCase(),
                "api/admin/user"
        ));
        return userService.deleteUser(user1);


    }

    @Transactional
    @PutMapping("/user/access")
    public Map<String, String> lockUser(@AuthenticationPrincipal UserDetailsImpl admin, @RequestBody Map<String, String> input) {
        User user1 = userService.findUserByEmail(input.get("user"));
        String operation = input.get("operation");

        if (user1 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        switch (operation) {
            case "LOCK":
                userService.lock(user1);
                logService.saveLog(new Log(
                        "LOCK_USER",
                        admin.getUsername().toLowerCase(),
                        String.format("Lock user %s", user1.getEmail().toLowerCase()),
                        "api/admin/user/access"

                ));
                return Map.of("status",
                        String.format("User %s locked!", user1.getEmail().toLowerCase()));
            case "UNLOCK":
                userService.unlock(user1);
                logService.saveLog(new Log(
                        "UNLOCK_USER",
                        admin.getUsername().toLowerCase(),
                        String.format("Unlock user %s", user1.getEmail().toLowerCase()),
                        "api/admin/user/access"

                ));
                return Map.of("status",
                        String.format("User %s unlocked!", user1.getEmail().toLowerCase()));
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation must be only LOCK or UNLOCK!");
        }


    }

    @GetMapping("/user")
    public List<UserDTO> getAll() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOList = users.stream().map(UserDTO::new).collect(Collectors.toList());
        return userDTOList;
    }

}

