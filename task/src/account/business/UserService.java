package account.business;

import account.persistence.RoleRepository;
import account.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    LogService logService;

    public static final int maxFailedAttempts = 4;


    public List<String> breachedPass = List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User toSave) {
        checkPassword(toSave.getPassword());
        Role role;
        if (userRepository.countAdmin(1L) == null) {
            role = roleRepository.findByName("ROLE_ADMINISTRATOR")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found!"));

        } else {
            role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found!"));

        }
        toSave.addToRole(role);
        String encodedPassword = passwordEncoder.encode((toSave.getPassword()));
        toSave.setPassword(encodedPassword);
        return userRepository.save(toSave);
    }

    public void checkPassword(String pass) {
        if (breachedPass.contains(pass)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
        if (pass.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");
        }
    }

    public User changePass(User user, String new_password) {
        if (passwordEncoder.matches(new_password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
        checkPassword(new_password);
        String encodedPassword = passwordEncoder.encode(new_password);
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public User grantRole(User user, String role) {
        Role newRole = roleRepository.findByName("ROLE_" + role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        Set<Role> userRoles = user.getRoles();
        if (userRoles.contains(getAccountant()) && newRole.isAdministrative())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        else if (userRoles.contains(getAdmin()) && newRole.isBusiness())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        else if (newRole.isAdministrative())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot grant 2 admins!");

        user.addToRole(newRole);
        return userRepository.save(user);

    }

    public User deleteRole(User user, String role) {
        Role oldRole = roleRepository.findByName("ROLE_" + role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        Set<Role> userRoles = user.getRoles();
        if (!userRoles.contains(oldRole))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        else if (oldRole.getName().equals("ROLE_ADMINISTRATOR"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        if (userRoles.size() < 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        userRoles.remove(oldRole);

        return userRepository.save(user);

    }


    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findUserByEmailIgnoreCase(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }
    }

    public Map<String, String> deleteUser(User user1) {
        Set<Role> userRoles = user1.getRoles();
        if (userRoles.contains(getAdmin()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        userRepository.delete(user1);
        return Map.of("user", user1.getEmail().toLowerCase()
                , "status", "Deleted successfully!");

    }

    public Role getAccountant() {
        Role role = roleRepository.findByName("ROLE_ACCOUNTANT")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));
        return role;
    }

    public Role getAdmin() {
        Role role = roleRepository.findByName("ROLE_ADMINISTRATOR")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));
        return role;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        userRepository.save(user);
    }

    public void resetFailedAttempts(User user) {
        int newFailAttempts = 0;
        user.setFailedAttempt(newFailAttempts);
        userRepository.save(user);
    }
    public void lock(User user) {
        if (user.hasRole("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }
        user.setLocked(true);
        userRepository.save(user);
    }
    public void unlock(User user) {
        resetFailedAttempts(user);
        user.setLocked(false);
        userRepository.save(user);
    }
}
