package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OwnedGameDTO {
    private int sysIdOwnedGame;
    private Timestamp ownedDate;
    private int userId;
    private int gameId;
}