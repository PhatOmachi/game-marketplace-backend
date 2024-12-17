package poly.gamemarketplacebackend.core.service;

import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.entity.ChatChannel;
import poly.gamemarketplacebackend.core.entity.Message;

import java.util.List;


public interface ChatService {
    ChatChannel findOrCreateChannel (String username);

    Message saveMessage (ChatChannel channel, String sender, String content);

    List<Message> findByChannel(String channel);

    List<Message> findByTatCa();

    void closeChannel (String username);

    List<ChatChannel> findAll();
}
