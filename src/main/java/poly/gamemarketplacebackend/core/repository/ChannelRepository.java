package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.ChatChannel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<ChatChannel, Integer> {
    Optional<ChatChannel> findByChannelName(String channel_name);

    Optional<ChatChannel> findByChannelNameAndStatus(String channelName, String status);

    List<ChatChannel> findAll();
}
