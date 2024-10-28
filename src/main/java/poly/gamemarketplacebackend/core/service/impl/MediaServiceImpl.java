package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.repository.MediaRepository;
import poly.gamemarketplacebackend.core.service.MediaService;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    @Override
    public void saveMedia(String mediaName, String mediaUrl, Integer sysIdGame) {
        mediaRepository.insertMedia(mediaName, mediaUrl, sysIdGame);
    }
}
