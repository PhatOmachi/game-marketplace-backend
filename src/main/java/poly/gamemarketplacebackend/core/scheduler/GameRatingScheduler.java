package poly.gamemarketplacebackend.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import poly.gamemarketplacebackend.core.repository.GameRepository;

@Component
@RequiredArgsConstructor
public class GameRatingScheduler {
    private static final Logger log = LoggerFactory.getLogger(GameRatingScheduler.class);
    private final GameRepository gameRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateGameRatings() {
        log.info("Updating game ratings...");
        gameRepository.updateGameRatings();
    }
}