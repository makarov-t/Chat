// ClientTest.java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.netology.Client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    @TempDir
    Path tempDir;

    @Test
    void testReadValidSettingsHost() throws Exception {
        File settingsFile = tempDir.resolve("settings.txt").toFile();
        Files.writeString(settingsFile.toPath(), "host=localhost\nport=9090");

        Client client = new Client();
        client.readSettings(settingsFile.toString());

        assertEquals("localhost", client.getHost());
    }

    @Test
    void testReadValidSettingsPort() throws Exception {
        File settingsFile = tempDir.resolve("settings.txt").toFile();
        Files.writeString(settingsFile.toPath(), "localhost\nport=5588");

        Client client = new Client();
        client.readSettings(settingsFile.toString());

        assertEquals(5588, client.getPort());
    }

    @Test
    void testLogMessageUser() throws IOException {
        // Указываем путь к файлу внутри tempDir
        Path logPath = tempDir.resolve("file.log");

        // Создаем все родительские директории (если нужно)
        Files.createDirectories(logPath.getParent());

        // Переопределяем путь к логу в сервере (важно!)
        Client.LOG_FILE = logPath.toString();

        Client client = new Client();
        client.logMessage("testUser", "Hello World");

        String logContent = Files.readString(logPath);
        assertTrue(logContent.contains("testUser"));
    }

    @Test
    void testLogMessageMessage() throws IOException {
        // Указываем путь к файлу внутри tempDir
        Path logPath = tempDir.resolve("file.log");

        // Создаем все родительские директории (если нужно)
        Files.createDirectories(logPath.getParent());

        // Переопределяем путь к логу в сервере (важно!)
        Client.LOG_FILE = logPath.toString();

        Client client = new Client();
        client.logMessage("testUser", "Hello World");

        String logContent = Files.readString(logPath);
        assertTrue(logContent.contains("Hello World"));
    }
}
