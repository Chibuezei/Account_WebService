package account.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import account.business.User;
import account.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user);
        }
    }

}