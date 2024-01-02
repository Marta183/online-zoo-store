package kms.onlinezoostore.utils;

import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.security.UserInfoDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

public class UsersUtil {
    public static User extractUser(@NotNull Principal connectedUser) {
        Object principal = ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return extractUser((UserInfoDetails) principal);
    }

    public static User extractUser(UserDetails userDetails) {
        return ((UserInfoDetails) userDetails).getUser();
    }
}
