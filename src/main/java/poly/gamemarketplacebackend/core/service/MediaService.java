package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.MediaDTO;

import java.util.List;

public interface MediaService {
    void saveMedia(String mediaName, String mediaUrl, Integer sysIdGame);

    int deleteMediaByGameId(Integer sysIdGame);

    int deleteMediaByUrl(String mediaUrl);

    List<MediaDTO> getMediaByGameId(Integer sysIdGame);
}
