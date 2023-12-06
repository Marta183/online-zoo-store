package kms.onlinezoostore.utils;

import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.security.UserInfoDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

public class UsersUtil {
    public static User extractUser(@NotNull Principal connectedUser) {
        Object principal = ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return  ((UserInfoDetails) principal).getUser();
    }
}
