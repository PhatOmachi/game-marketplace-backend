package poly.gamemarketplacebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GameMarketplaceBackend {

    public static void main(String[] args) {
        SpringApplication.run(GameMarketplaceBackend.class, args);
    }

}
