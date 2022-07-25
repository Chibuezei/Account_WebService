package account.security;

import account.business.User;
import account.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    UserService userService;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        String email;
        try {
            email = new String(Base64.getDecoder().decode(request.getHeader("authorization")
                    .split(" ")[1])).split(":")[0];
        } catch (Exception e) {
            email = request.getRemoteUser();
        }
        System.out.println(email);
        User user = userService.findUserByEmail(email);
        String message;
        if (user == null) {
            message = "Access Denied!";
        }
        else if (user.isLocked() || user.getFailedAttempt() > 5) {
            message = "User account is locked";
        }
        else {
            message = "Access Denied!";
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}