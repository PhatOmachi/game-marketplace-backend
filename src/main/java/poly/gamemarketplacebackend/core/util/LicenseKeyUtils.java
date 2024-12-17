package poly.gamemarketplacebackend.core.util;

import java.security.SecureRandom;

public class LicenseKeyUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int GROUP_SIZE = 5;
    private static final int GROUP_COUNT = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateLicenseKey() {
        StringBuilder licenseKey = new StringBuilder();

        for (int i = 0; i < GROUP_COUNT; i++) {
            if (i > 0) {
                licenseKey.append("-");
            }
            for (int j = 0; j < GROUP_SIZE; j++) {
                int index = RANDOM.nextInt(CHARACTERS.length());
                licenseKey.append(CHARACTERS.charAt(index));
            }
        }

        return licenseKey.toString();
    }
}
