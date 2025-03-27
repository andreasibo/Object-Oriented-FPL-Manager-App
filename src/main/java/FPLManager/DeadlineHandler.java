package FPLManager;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * The DeadlineHandler class calculates and handles the deadline for a game week in the Fantasy Premier League (FPL) game.
 */
public class DeadlineHandler {
    private String gwDeadline;

    /**
     * Constructs a new DeadlineHandler object with the specified game week information.
     *
     * @param gwInfo a list containing the game week information
     */
    public DeadlineHandler(List<Map<String, Object>> gwInfo) {
        setGWDeadline(gwInfo);
    }

    /**
     * Sets the game week deadline using the provided game week information.
     *
     * @param gwInfo a list containing the game week information
     */
    private void setGWDeadline(List<Map<String, Object>> gwInfo) {
        if (gwInfo == null || gwInfo.isEmpty()) {
            System.err.println("Error: gwInfo list is null or empty.");
            return;
        }
    
        String firstKickOff = (String) gwInfo.get(0).get("kickoff_time");
    
        if (firstKickOff == null) {
            System.err.println("Error: kickoff_time is null in gwInfo.");
            return;
        }
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(firstKickOff);
        ZonedDateTime zonedDateTimeOslo = offsetDateTime.atZoneSameInstant(ZoneId.of("Europe/Oslo"));
        LocalDateTime localDateTime = zonedDateTimeOslo.toLocalDateTime();
    
        LocalDateTime deadline = localDateTime.minusMinutes(90);
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM");
        String formattedDeadline = deadline.format(formatter);
        this.gwDeadline = formattedDeadline;
    }

    // Getter
    public String getGWDeadLine() { return this.gwDeadline; }

}
