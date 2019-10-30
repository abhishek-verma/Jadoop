package com.abhishek.client;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;

    public Client(String host, int port, String file) {
        try {
            System.out.println("Connecting to Server...");
            socket = new Socket(host, port);
            sendFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String filepath) throws IOException {
        File myFile = new File(filepath);
        int count;
        byte[] buffer = new byte[1024];

        OutputStream out = socket.getOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(myFile));

        System.out.println("Sending File...");
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
            out.flush();
        }

        socket.close();
        in.close();

        System.out.println("File successfully Sent!");
    }

    public static void main(String[] args) {
        Client fc = new Client("localhost", 1988, "files/cat.jpg");
    }
}
