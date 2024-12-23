package poly.gamemarketplacebackend.core.security.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.entity.FailedLoginAttempt;
import poly.gamemarketplacebackend.core.repository.FailedLoginAttemptRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomLoginAttemptService {

    private final FailedLoginAttemptRepository failedLoginAttemptRepository;

    public void loginFailed(String username) {
        FailedLoginAttempt attempt = failedLoginAttemptRepository.findByUsername(username)
                .orElse(FailedLoginAttempt.builder()
                        .username(username)
                        .failedAttempts(0)
                        .lastFailedTime(LocalDateTime.now())
                        .build());

        attempt.setFailedAttempts(attempt.getFailedAttempts() + 1);
        attempt.setLastFailedTime(LocalDateTime.now());
        failedLoginAttemptRepository.save(attempt);
    }

    @Transactional
    public boolean isBlocked(String username) {
        Optional<FailedLoginAttempt> attemptOpt = failedLoginAttemptRepository.findByUsername(username);

        if (attemptOpt.isPresent()) {
            FailedLoginAttempt attempt = attemptOpt.get();

            int MAX_ATTEMPTS = 5;
            if (attempt.getFailedAttempts() >= MAX_ATTEMPTS) {
                long lastFailedTime = attempt.getLastFailedTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long currentTime = System.currentTimeMillis();

                // 30 minutes
                long LOCK_TIME_DURATION = 30 * 60 * 1000;
                if ((currentTime - lastFailedTime) < LOCK_TIME_DURATION) {
                    return true;
                } else {
                    resetFailedAttempts(username);
                }
            }
        }
        return false;
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        failedLoginAttemptRepository.deleteByUsername(username);
    }
}

