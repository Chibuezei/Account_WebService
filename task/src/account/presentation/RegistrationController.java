package account.presentation;

import account.business.Log;
import account.business.LogService;
import account.business.User;
import account.business.UserService;
import account.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("api/auth")
public class RegistrationController {
    private PasswordEncoder passwordEncoder;

    public RegistrationController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private User user;
    @Autowired
    UserService userService;
    @Autowired
    LogService logService;


    @PostMapping("/signup")
    public UserDTO addUser(@Valid @RequestBody User user) {
        if (userService.findUserByEmail(user.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        User toSave = new User(user.getName(), user.getLastname(), user.getEmail(), user.getPassword());
        userService.save(toSave);
        logService.saveLog(new Log(
                "CREATE_USER",
                "Anonymous",
                toSave.getEmail().toLowerCase(),
                "api/auth/signup"

        ));
        return new UserDTO(toSave);
    }

    @PostMapping("/changepass")
    public Map<String, String> changePass(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, String> input) {
        User user = userService.findUserByEmail(userDetails.getUsername());
        userService.changePass(user, input.get("new_password"));
        logService.saveLog(new Log(
                "CHANGE_PASSWORD",
                user.getEmail().toLowerCase(),
                user.getEmail().toLowerCase(),
                "api/auth/changepass"

        ));
        return Map.of("email", user.getEmail().toLowerCase()
                , "status", "The password has been updated successfully");

    }

}
