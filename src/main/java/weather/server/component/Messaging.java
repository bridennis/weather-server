package weather.server.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import weather.server.RestApplication;
import weather.server.entity.ResponseMessage;

@Component
public class Messaging {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    Logger log = LoggerFactory.getLogger(RestApplication.class);

    public Messaging() {
        messagingTemplate = null;
    }

    public void sendMessage(String destination, ResponseMessage message) {
        messagingTemplate.convertAndSend(destination, message);
        log.info("Send message [{}] to [{}]", message, destination);
    }
}
