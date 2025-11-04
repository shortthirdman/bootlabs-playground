package com.shortthirdman.bootlabs.messaging.websockets.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class SessionCleanupListener implements ApplicationListener<SessionDisconnectEvent> {
    @Override
    public void onApplicationEvent(@NonNull SessionDisconnectEvent event) {
        log.info("Session disconnected [{}]: {}", event.getUser(), event.getSessionId());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}
