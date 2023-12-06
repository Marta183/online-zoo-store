package kms.onlinezoostore.notifications.messages;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Lazy
@Component
public class MessageBuilder {
    private final Map<MessageType, AbstractMessage> messageBuilders;

    public MessageBuilder() {
        this.messageBuilders = new HashMap<>();
        messageBuilders.put(MessageType.REGISTRATION_CONFIRMATION, new RegistrationConfirmationMessage());
        messageBuilders.put(MessageType.PASSWORD_RESET_CONFIRMATION, new PasswordResetConfirmationMessage());
    }

    public Object build(MessageType messageType, Map<String,String> params) {
        return messageBuilders.get(messageType).build(params);
    }
}
