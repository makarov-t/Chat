package ru.netology;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private static final String SETTINGS_FILE = "client/src/main/resources/settings.txt";
    public static String LOG_FILE = "client/src/main/resources/file.log";
    private String host;
    private int port;
    private String username;

    public static void main (String [] args) {
        new Client().start();
    }


    public void start() {
        readSettings(SETTINGS_FILE);
        Scanner scanner = new Scanner(System.in);
        System.out.println("host= " + host + ", port= " + port);
        System.out.print("Введите ваше имя: ");
        username = scanner.nextLine();

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(username);

            // Флаг для управления потоком чтения
            AtomicBoolean running = new AtomicBoolean(true);

            Thread readerThread = new Thread(() -> {
                try {
                    String message;
                    while (running.get() && (message = in.readLine()) != null) {
                        System.out.println(message);
                        logMessage("Received", message);
                    }
                } catch (IOException e) {
                    if (running.get()) e.printStackTrace();
                }
            });
            readerThread.start();

            String input;
            while (running.get()) {
                input = scanner.nextLine();
                if ("/exit".equalsIgnoreCase(input)) {
                    running.set(false); // Сигнал к остановке
                    break;
                }
                out.println(input);
                logMessage("Sent", input);
            }

            // Закрываем ресурсы явно
            socket.close();
            readerThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*public void start() {
        readSettings(SETTINGS_FILE);
        Scanner scanner = new Scanner((System.in));
        System.out.println("host= " + host + ", port= " + port);
        System.out.print("Введите ваше имя: ");
        username = scanner.nextLine();

        try (Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(username);
            new Thread (() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                        logMessage("Received", message);
                    }
                } catch (IOException e ) {
                    e.printStackTrace();
                }
            }).start();

            String input;
            while (true) {
                input = scanner.nextLine();
                if ("/exit".equalsIgnoreCase(input)) {
                    break;
                }
                out.println(input);
                logMessage("Sent", input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void readSettings(String settingsFile) {
        try (Scanner scanner = new Scanner(new File(settingsFile))) {
            //String line = String.valueOf(scanner);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("host=")) {
                    host = line.substring(5);
                } else if (line.startsWith("port=")) {
                    port = Integer.parseInt(line.substring(5));
                }

            }

        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден. Используется данные по умолчанию: localhost: 1234");
            host = "localhost";
            port = 1234;

        }
    }

    public void logMessage(String type, String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(String.format("%s [%s] %s\n", new Date(), type, message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
