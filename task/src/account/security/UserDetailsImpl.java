package account.security;

import account.business.Role;
import account.business.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

//@Data
//@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private User user;


    public UserDetailsImpl(User user) {
        this.user = user;
    }


    public UserDetailsImpl() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return getGrantedAuthorities(user.getRoles());
    }

    private List<SimpleGrantedAuthority> getGrantedAuthorities(Set<Role> UserRole) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : UserRole) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
