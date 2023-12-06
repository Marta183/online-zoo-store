package kms.onlinezoostore.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AuthenticationMainEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(@NotNull HttpServletRequest request,
                         @NotNull HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

//        log.error("unauthorized", authException);
//
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
//                "You need to login first in order to perform this action."
//        );
    }
}
