package account.exception;

import account.business.Log;
import account.business.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    LogService logService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException {
        String user = request.getRemoteUser();
        if (user == null) user = "Anonymous";
        logService.saveLog(new Log(
                "ACCESS_DENIED",
                user.toLowerCase(),
                request.getRequestURI(),
                request.getRequestURI()));
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }
}
