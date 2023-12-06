package kms.onlinezoostore.notifications.messages;

import java.util.Map;

public abstract class AbstractMessage {

    public abstract Object build(Map<String,String> params);
}
