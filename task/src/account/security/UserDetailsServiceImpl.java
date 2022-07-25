package account.security;

import account.business.User;
import account.exception.AccountStatusUserDetailsChecker;
import account.exception.UserNotFoundException;
import account.persistence.RoleRepository;
import account.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    DataLoader dataLoader;


    @Override
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmailIgnoreCase(email);
        if (user.isPresent()) {
            UserDetailsImpl userDetails =  new UserDetailsImpl(user.get());
//            detailsChecker().check(userDetails);
            return userDetails;
        }
        else throw new UsernameNotFoundException("Not found: " + email);
    }
//    @Bean
//    private AccountStatusUserDetailsChecker detailsChecker(){
//        return new AccountStatusUserDetailsChecker();
//    }


}
