package poly.gamemarketplacebackend.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.entity.ChatChannel;
import poly.gamemarketplacebackend.core.entity.Message;
import poly.gamemarketplacebackend.core.repository.ChannelRepository;
import poly.gamemarketplacebackend.core.repository.MessageRepository;
import poly.gamemarketplacebackend.core.service.ChatService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChannelRepository chatChannelRepository;

    @Autowired
    private MessageRepository messageRepository;


    @Override
    public ChatChannel findOrCreateChannel(String username) {
        ChatChannel chatChannel = chatChannelRepository.findByChannelName(username).orElse(null);
        if (chatChannel == null) {
            chatChannel = new ChatChannel();
            chatChannel.setChannelName(username);
            chatChannel.setStatus("open");
            chatChannel.setCreatedAt(LocalDateTime.now());
            chatChannelRepository.save(chatChannel);
        }
        return chatChannel;
    }

    @Override
    public Message saveMessage(ChatChannel channel, String sender, String content) {
        Message message = new Message();
        message.setChannel(channel);
        message.setSenderName(sender);
        message.setContent(content);

        messageRepository.save(message);
        return null;
    }

    @Override
    public void closeChannel(String username) {
        ChatChannel channel = chatChannelRepository.findByChannelName(username).orElseThrow(() -> new RuntimeException("Channel not found"));
        channel.setStatus("closed");
        chatChannelRepository.save(channel);
    }

    @Override
    public List<Message> findByChannel(String channelName) {
        return messageRepository.findByChannelString(channelName).orElse(null);
    }

    @Override
    public List<Message> findByTatCa() {
        return messageRepository.findByTatCa().orElse(null);
    }

    @Override
    public List<ChatChannel> findAll() {
        return chatChannelRepository.findAll();
    }
}
