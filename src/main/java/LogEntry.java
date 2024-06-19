import java.time.LocalTime;

public class LogEntry {
    LocalTime time;
    String user;
    String action;

    LogEntry(LocalTime time, String user, String action) {
        this.time = time;
        this.user = user;
        this.action = action;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }

}
