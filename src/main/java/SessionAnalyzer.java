import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class SessionAnalyzer {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Pattern LOG_PATTERN = Pattern.compile("^(\\d{2}:\\d{2}:\\d{2})\\s(\\w+)\\s(Start|End)$");
    private static LocalTime earliestTime = null;
    private static LocalTime latestTime = null;

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java SessionAnalyzer <logFilePath>");
            System.exit(1);
        }

        String logFilePath = args[0];
        List<LogEntry> logEntries = parseLogEntries(logFilePath);

        if (logEntries.isEmpty()) {
            System.out.println("No valid log entries found.");
            return;
        }

        Map<String, UserSession> userSessions = analyzeSessions(logEntries);
        printSessionSummary(userSessions);

    }

    private static List<LogEntry> parseLogEntries(String logFilePath) {
        List<LogEntry> logEntries = new ArrayList<>();


        try (BufferedReader reader = Files.newBufferedReader(Paths.get(logFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    LocalTime time = LocalTime.parse(matcher.group(1), TIME_FORMATTER);
                    String user = matcher.group(2);
                    String action = matcher.group(3);

                    logEntries.add(new LogEntry(time, user, action));

                    if (earliestTime == null || time.isBefore(earliestTime)) {
                        earliestTime = time;
                    }
                    if (latestTime == null || time.isAfter(latestTime)) {
                        latestTime = time;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return logEntries;
    }

    private static Map<String,UserSession> analyzeSessions(List<LogEntry> entries) {
        Map<String, UserSession> userSessions = new HashMap<>();
        earliestTime = entries.get(0).getTime();
        latestTime = entries.get(entries.size() - 1).getTime();

        for (LogEntry entry : entries) {
            userSessions.computeIfAbsent(entry.getUser(), UserSession::new).processEntry(entry, earliestTime);
        }

        for (UserSession session : userSessions.values()) {
            session.finishRemainingSessions(latestTime);
        }
        return userSessions;
    }

    private static void printSessionSummary(Map<String,UserSession> userSessions) {
        for (UserSession session : userSessions.values()) {
            System.out.printf("%s %d %d%n", session.user, session.getSessionCount(), session.getTotalDuration());
        }
    }

}
