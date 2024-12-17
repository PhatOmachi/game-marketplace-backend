package poly.gamemarketplacebackend.core.Chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.entity.ChatChannel;
import poly.gamemarketplacebackend.core.entity.Message;
import poly.gamemarketplacebackend.core.service.ChatService;
import poly.gamemarketplacebackend.core.service.impl.ChatServiceImpl;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    @Autowired
    private ChatService chatService;

    // Quản lý người dùng: sessionId -> username
    private final ConcurrentHashMap<String, String> userSessions = new ConcurrentHashMap<>();
    // Quản lý session: username -> WebSocketSession
    private final ConcurrentHashMap<String, WebSocketSession> sessionsByUsername = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        System.out.println("User connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // Xử lý tin nhắn theo định dạng: "type:username:message"
        String[] parts = payload.split(":");
        if (parts.length < 2) return;

        String type = parts[0]; // "join" hoặc "message"
        String target = parts[1]; // Tên người dùng (hoặc "all")
        String content = parts.length == 4 ? parts[3] : "";

        if ("loadMessages".equals(type)) {
            if (target.equals("ADMIN") || target.equals("STAFF")) {
                List<Message> messages = chatService.findByTatCa(); // Load dữ liệu từ DB
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                String json = objectMapper.writeValueAsString(messages);
                session.sendMessage(new TextMessage(json));
            }else {
                List<Message> messages = chatService.findByChannel(target); // Load dữ liệu từ DB
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                String json = objectMapper.writeValueAsString(messages);
                session.sendMessage(new TextMessage(json));
            }
        }

        if (type.equals("join")) {
            // Gán username cho session
            userSessions.put(session.getId(), target);
            sessionsByUsername.put(target, session);
//            System.out.println(target + " joined.");
            chatService.findOrCreateChannel(target);
        } else if (type.equals("message")) {
            // Gửi tin nhắn cho người dùng cụ thể
            WebSocketSession recipientSession = sessionsByUsername.get(target);
            String sender = parts[2];
//            System.out.println("Payload: " + target);


            if (target.equals("ADMIN") || target.equals("STAFF")) {
                ChatChannel channel = chatService.findOrCreateChannel(sender);
                chatService.saveMessage(channel, sender, content);
            }else {
                ChatChannel channel = chatService.findOrCreateChannel(target);
                chatService.saveMessage(channel, sender, content);
            }
            if (recipientSession != null && recipientSession.isOpen()) {
                if (target.equals("ADMIN") || target.equals("STAFF")) {
                    Message messages = new Message(sender, null, content);
                    recipientSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(messages)));
                }else {
                    Message messages = new Message(sender,null,content);
                    recipientSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(messages)));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String username = userSessions.remove(session.getId());
        if (username != null) {
            sessionsByUsername.remove(username);
//            System.out.println(username + " disconnected.");
        }
    }


}
