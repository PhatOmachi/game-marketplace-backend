package poly.gamemarketplacebackend.core.util;

public class OTPUtil {

    public static String generateOTP() {
        int randomPin = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(randomPin);
    }
}
