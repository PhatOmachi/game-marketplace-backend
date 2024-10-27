package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

@Data
public class MediaDTO {
    private Integer sysIdMedia;
    private String mediaName;
    private String mediaUrl;
    private Integer sysIdGame;
}
