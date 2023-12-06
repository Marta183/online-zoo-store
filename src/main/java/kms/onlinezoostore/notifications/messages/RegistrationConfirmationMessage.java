package kms.onlinezoostore.notifications.messages;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import java.util.Map;

public class RegistrationConfirmationMessage extends AbstractMessage {

    @Value("${spring.mail.username}")
    private String mailSenderAddress;

    private final String verificationEndpoint = "/verify-email";

    public SimpleMailMessage build(Map<String,String> params) {
        String verificationUrl = params.get("verificationLink").replace("{endpoint}", verificationEndpoint);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(params.get("userEmail"));
        mailMessage.setFrom(mailSenderAddress);
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("Hi, " + params.get("username") + ", \n" +
                "Thank you for registering with us, \n" +
                "Please, follow the link below to complete your registration. \n" +
                verificationUrl + "\n\n" +
                "Verify your email to activate your account. \n"+
                "Thank you!");
        return mailMessage;
    }
}
