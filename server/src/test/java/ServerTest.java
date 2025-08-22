// ServerTest.java


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.netology.Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.Server.*;
import static ru.netology.Server.SETTINGS_FILE;

class ServerTest {
    private static final String SETTINGS_FILE = "server/src/main/resources/settings.txt";
    int port;

    @TempDir
    Path tempDir;

    @Test
    void testReadPortFromValidSettings() throws IOException {
        File settingsFile = tempDir.resolve("settings.txt").toFile();
        Files.writeString(settingsFile.toPath(), "port=5588");

        Server server = new Server();
        port = server.readPortFromSettings(settingsFile.toString());
        int portExpected = 5588;

        assertEquals(portExpected, port);
    }

    @Test
    void testReadPortFromInvalidSettings() {
        Server server = new Server();
        int port = readPortFromSettings(SETTINGS_FILE);


        assertEquals(1234, port);
    }

    @Test
    void testLogMessageUser() throws IOException {
        // Указываем путь к файлу внутри tempDir
        Path logPath = tempDir.resolve("file.log");

        // Создаем все родительские директории (если нужно)
        Files.createDirectories(logPath.getParent());

        // Переопределяем путь к логу в сервере (важно!)
        Server.LOG_FILE = logPath.toString();

        Server server = new Server();
        server.logMessage("testUser", "Hello World");

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
        Server.LOG_FILE = logPath.toString();

        Server server = new Server();
        server.logMessage("testUser", "Hello World");

        String logContent = Files.readString(logPath);
        assertTrue(logContent.contains("Hello World"));
    }
}




