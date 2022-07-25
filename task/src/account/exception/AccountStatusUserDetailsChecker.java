package account.exception;



import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

/**
 * @author Luke Taylor
 */
public class AccountStatusUserDetailsChecker implements UserDetailsChecker, MessageSourceAware {


    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public void check(UserDetails user) {
        System.out.println("here");
        if (!user.isAccountNonLocked()) {
            System.out.println("here");
            throw new LockedException(
                    this.messages.getMessage("AccountStatusUserDetailsChecker.locked", "User account is locked"));
        }
        if (!user.isEnabled()) {
            throw new DisabledException(
                    this.messages.getMessage("AccountStatusUserDetailsChecker.disabled", "User is disabled"));
        }
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException(
                    this.messages.getMessage("AccountStatusUserDetailsChecker.expired", "User account has expired"));
        }
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(this.messages
                    .getMessage("AccountStatusUserDetailsChecker.credentialsExpired", "User credentials have expired"));
        }
    }

    /**
     * @since 5.2
     */
    @Override
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }

}