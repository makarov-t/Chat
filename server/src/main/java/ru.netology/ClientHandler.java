package ru.netology;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            username = in.readLine();
            Server.broadcastMessage(username + " присоединился к чату!", this);

            String message;
            while ((message = in.readLine()) != null) {
                if ("/exit".equalsIgnoreCase(message)) {
                    Server.broadcastMessage(username + " покинул чат.", this);
                    break;
                }
                Server.broadcastMessage(message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.broadcastMessage(username + " покинул чат.", this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }
}