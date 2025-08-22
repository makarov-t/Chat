package ru.netology;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private volatile boolean isRunning;
    public static final String SETTINGS_FILE = "server/src/main/resources/settings.txt";
    public static String LOG_FILE = "server/src/main/resources/file.log";
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();


    public static void main(String[] args) {

        Server server = new Server();
        server.start();
        /*try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void start() {
        int port = readPortFromSettings(SETTINGS_FILE);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            isRunning = true;
            System.out.println("Server started on port " + port);
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isRunning = false;
    }

    public static int readPortFromSettings(String settingsFile) {
        try (Scanner scanner = new Scanner(new File(settingsFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("port=")) {
                    return Integer.parseInt(line.substring(5));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Settings file not found. Using default port 1234");
            return 1234;
        }
        return 1234;
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        String formattedMessage = String.format("[%s] %s", sender.getUsername(), message);
        for (ClientHandler client : clients) {
            client.sendMessage(formattedMessage);
        }
        logMessage(sender.getUsername(), message);
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void logMessage(String username, String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(String.format("%s [%s] %s\n", new Date(), username, message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}