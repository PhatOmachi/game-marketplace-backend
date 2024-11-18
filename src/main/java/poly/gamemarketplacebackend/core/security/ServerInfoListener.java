package poly.gamemarketplacebackend.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ServerInfoListener implements ApplicationListener<WebServerInitializedEvent> {

    private static int port;

    @Value("${server.address:localhost}")
    private static String host;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        port = event.getWebServer().getPort();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logServerInfo() {
        System.out.println("Application is running on host: " + host + " and port: " + port);
    }

    public static String getAddress() {
        return host + ":" + port;
    }
}