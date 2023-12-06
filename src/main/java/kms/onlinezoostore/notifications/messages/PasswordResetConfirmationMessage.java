package kms.onlinezoostore.notifications.messages;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import java.util.Map;

public class PasswordResetConfirmationMessage extends AbstractMessage {

    @Value("${spring.mail.username}")
    private String mailSenderAddress;

    private final String verificationEndpoint = "/reset-password";

    public SimpleMailMessage build(Map<String,String> params) {
        String verificationUrl = params.get("verificationLink").replace("{endpoint}", verificationEndpoint);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(params.get("userEmail"));
        mailMessage.setFrom(mailSenderAddress);
        mailMessage.setSubject("Password Recovery!");
        mailMessage.setText("Hi, " + params.get("username") + ", \n" +
                "Please, follow the link below to verify your email address. \n" +
                verificationUrl + "\n\n" +
                "Verify your email to farther password recovery. \n"+
                "Thank you!");
        return mailMessage;
    }
}
