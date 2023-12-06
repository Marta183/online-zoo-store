package kms.onlinezoostore.utils;

import jakarta.servlet.http.HttpServletRequest;

public class ApplicationUtil {
    public static String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":"
                + request.getServerPort()
                + request.getContextPath();
    }
}
