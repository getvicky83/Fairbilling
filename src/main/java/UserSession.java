import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Deque;

public class UserSession {
    String user;
    int sessionCount = 0;
    long totalDuration = 0;
    Deque<LocalTime> startTimes = new ArrayDeque<>();

    UserSession(String user) {
        this.user = user;
    }

    void processEntry(LogEntry entry, LocalTime earliestTime) {
        if (entry.action.equals("Start")) {
            startTimes.push(entry.time);
        } else if (entry.action.equals("End")) {
            if (startTimes.isEmpty()) {
                totalDuration += Duration.between(earliestTime, entry.time).getSeconds();
            } else {
                LocalTime startTime = startTimes.pop();
                totalDuration += Duration.between(startTime, entry.time).getSeconds();
            }
            sessionCount++;
        }
    }

    void finishRemainingSessions(LocalTime latestTime) {
        while (!startTimes.isEmpty()) {
            LocalTime startTime = startTimes.pop();
            totalDuration += Duration.between(startTime, latestTime).getSeconds();
            sessionCount++;
        }
    }

    int getSessionCount() {
        return sessionCount;
    }

    long getTotalDuration() {
        return totalDuration;
    }
}
