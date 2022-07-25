package account.security;

import account.business.Log;
import account.business.LogService;
import account.business.User;
import account.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Component
//public class CustomLoginFailureHandler {
public class CustomLoginFailureHandler implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    LogService logService;

    @Override
//    @EventListener // ithis caused the event to be caught twice and logs written twice
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        try {
            String email = event.getAuthentication().getName();
            logService.saveLog(new Log(
                    "LOGIN_FAILED",
                    email.toLowerCase(),
                    request.getRequestURI(),
                    request.getRequestURI()
            ));
            User user = userService.findUserByEmail(email);
            if (user != null && !user.hasRole("ROLE_ADMINISTRATOR")) {
                if (!user.isLocked()) {
                    if (user.getFailedAttempt() < UserService.maxFailedAttempts) {
                        userService.increaseFailedAttempts(user);
                    } else {
                        userService.increaseFailedAttempts(user);
                        userService.lock(user);
                        logService.saveLog(new Log(
                                "BRUTE_FORCE",
                                email.toLowerCase(),
                                request.getRequestURI(),
                                request.getRequestURI()

                        ));
                        logService.saveLog(new Log(
                                "LOCK_USER",
                                email.toLowerCase(),
                                String.format("Lock user %s", email.toLowerCase()),
                                request.getRequestURI()

                        ));
//
                    }
                }
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomLoginFailure");
        }

    }


}