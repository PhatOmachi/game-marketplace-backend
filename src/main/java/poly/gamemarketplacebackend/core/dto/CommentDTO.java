package poly.gamemarketplacebackend.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Integer sysIdComment;
    private String context;
    private LocalDateTime commentDate;
    private UsersDTO usersDTO;
    private Integer gameId;
    private Integer star;
}