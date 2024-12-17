package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.ChatChannel;
import poly.gamemarketplacebackend.core.entity.Message;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
    @Query(value = "select * from message where channel_name = :channel", nativeQuery = true)
    Optional<List<Message>> findByChannelString(@Param("channel") String channel);

    @Query(value = "select * from message", nativeQuery = true)
    Optional<List<Message>> findByTatCa();

}
