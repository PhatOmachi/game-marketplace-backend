package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.MediaDTO;
import poly.gamemarketplacebackend.core.mapper.MediaMapperImpl;
import poly.gamemarketplacebackend.core.repository.MediaRepository;
import poly.gamemarketplacebackend.core.service.MediaService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final MediaMapperImpl mediaMapperImpl;

    @Override
    public void saveMedia(String mediaName, String mediaUrl, Integer sysIdGame) {
        mediaRepository.insertMedia(mediaName, mediaUrl, sysIdGame);
    }

    @Override
    public int deleteMediaByGameId(Integer sysIdGame) {
        return mediaRepository.deleteMediaByGameId(sysIdGame);
    }

    @Override
    public int deleteMediaByUrl(String mediaUrl) {
        return mediaRepository.deleteMediaByUrl(mediaUrl);
    }

    @Override
    public List<MediaDTO> getMediaByGameId(Integer sysIdGame) {
        return mediaMapperImpl.toDTOs(mediaRepository.getMediaByGameId(sysIdGame));
    }


}
