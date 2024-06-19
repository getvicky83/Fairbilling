import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

public class SessionAnalyzerTest {

    @TempDir
    Path tempDir;
    private Path logFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        logFilePath = Files.createTempFile(tempDir, "session_log", ".txt");
    }

    @Test
    public void testSingleSessionWithMatchingTimes() throws IOException {
        String logData = "14:02:03 ALICE99 Start\n14:02:34 ALICE99 End\n";
        Files.write(logFilePath, logData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        String output = captureOutput(() -> SessionAnalyzer.main(new String[]{logFilePath.toString()}));
        assertTrue(output.contains("ALICE99 1 31"));
    }

    @Test
    public void testSingleSessionWithoutMatchingEndTime() throws IOException {
        String logData = "14:02:03 ALICE99 Start\n";
        Files.write(logFilePath, logData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        String output = captureOutput(() -> SessionAnalyzer.main(new String[]{logFilePath.toString()}));
        assertTrue(output.contains("ALICE99 1 0"));
    }

    @Test
    public void testMultipleSessionsForSingleUser() throws IOException {
        String logData = "14:02:03 ALICE99 Start\n14:02:34 ALICE99 End\n14:03:00 ALICE99 Start\n14:03:30 ALICE99 End\n";
        Files.write(logFilePath, logData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        String output = captureOutput(() -> SessionAnalyzer.main(new String[]{logFilePath.toString()}));
        assertTrue(output.contains("ALICE99 2 61"));
    }

    @Test
    public void testSessionsForMultipleUsers() throws IOException {
        String logData = "14:02:03 ALICE99 Start\n14:02:34 ALICE99 End\n14:03:00 BOB Start\n14:03:30 BOB End\n";
        Files.write(logFilePath, logData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        String output = captureOutput(() -> SessionAnalyzer.main(new String[]{logFilePath.toString()}));
        assertTrue(output.contains("ALICE99 1 31"));
        assertTrue(output.contains("BOB 1 30"));
    }

    @Test
    public void testInvalidLogEntriesIgnored() throws IOException {
        String logData = "Invalid log entry\n14:02:03 ALICE99 Start\n14:02:34 ALICE99 End\n";
        Files.write(logFilePath, logData.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        String output = captureOutput(() -> SessionAnalyzer.main(new String[]{logFilePath.toString()}));
        assertTrue(output.contains("ALICE99 1 31"));
    }
    private String captureOutput(Runnable runnable) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        runnable.run();
        System.setOut(originalOut);
        return outContent.toString();
    }

}