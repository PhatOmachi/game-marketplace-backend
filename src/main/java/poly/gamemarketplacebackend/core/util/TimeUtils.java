package poly.gamemarketplacebackend.core.util;

import java.sql.Timestamp;
import java.time.LocalDate;

public class TimeUtils {
    public static LocalDate toLocalDate(Timestamp timestamp) {
        return timestamp.toLocalDateTime().toLocalDate();
    }
}
