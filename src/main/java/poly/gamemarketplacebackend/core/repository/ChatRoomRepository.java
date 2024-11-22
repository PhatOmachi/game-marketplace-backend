package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.dto.ChatRoomDTO;
import poly.gamemarketplacebackend.core.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUserName(String userId);

    @Query(value = "SELECT * FROM ChatRoom", nativeQuery = true)
    List<Optional<ChatRoom>> timPhongChat();
}
