package it.niedermann.fis.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class SocketRegistry {

    private final Logger logger = LoggerFactory.getLogger(SocketRegistry.class);
    private final Set<String> listeners = new HashSet<>();

    public void add(String sessionId) {
        listeners.add(sessionId);
    }

    public void remove(String sessionId) {
        listeners.remove(sessionId);
    }

    public Collection<String> getListeners() {
        return listeners;
    }

    @EventListener
    public void sessionDisconnectionHandler(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        logger.info("Disconnecting " + sessionId + "!");
        remove(sessionId);
    }
}
