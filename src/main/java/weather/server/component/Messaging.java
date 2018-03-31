package weather.server.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import weather.server.entity.ResponseMessage;

@Component
public class Messaging {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    public Messaging() {
        messagingTemplate = null;
    }

    public void sendMessage(String destination, ResponseMessage message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}
