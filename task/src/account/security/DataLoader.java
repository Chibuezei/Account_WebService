package account.security;

import account.business.Role;
import account.persistence.RoleRepository;
import account.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            roleRepository.save(new Role("ROLE_ADMINISTRATOR"));
            roleRepository.save(new Role("ROLE_USER"));
            roleRepository.save(new Role("ROLE_ACCOUNTANT"));
            roleRepository.save(new Role("ROLE_AUDITOR"));
        } catch (Exception e) {

        }
    }
}