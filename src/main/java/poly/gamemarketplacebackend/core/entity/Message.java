package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "channel_name", referencedColumnName = "channel_name", nullable = false)
    private ChatChannel channel; // Tham chiếu đến channel_name

    private String senderName; // Lấy từ JWT

    private String senderRole; // Lấy từ JWT

    private String content; // Nội dung tin nhắn


    public Message(String senderName, String senderRole, String content) {
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.content = content;
    }
}
