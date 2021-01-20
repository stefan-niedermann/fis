package it.niedermann.fis.weather;

import it.niedermann.fis.socket.SocketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class WeatherDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(WeatherDispatcher.class);

    @Autowired
    private SocketRegistry socketRegistry;
    private final SimpMessagingTemplate template;

    public WeatherDispatcher(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Scheduled(fixedDelayString = "5000")
    public void dispatch() {
        final Collection<String> listeners = socketRegistry.getListeners();
        if (listeners.size() == 0) {
            logger.info("Skip weather poll because no listeners are registered.");
            return;
        }
        for (String listener : listeners) {
            logger.info("Sending notification to " + listener);

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(listener);
            headerAccessor.setLeaveMutable(true);
            template.convertAndSendToUser(
                    listener,
                    "/notification/weather",
                    "New weather arrived...",
                    headerAccessor.getMessageHeaders());
        }
    }
}
