package com.abhishek.client;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;

    public Client(String host, int port) throws IOException {
        System.out.println("Connecting to Server...");
        socket = new Socket(host, port);
    }

    public void sendAction(String action) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(action);
        dataOutputStream.flush(); // send the message
    }

    public void putFile(String filepath) throws IOException {
        File file = new File(filepath);
        int count;
        byte[] buffer = new byte[1024];

        // Sending file name
        sendFileName(file.getName());

        // Sending the file
        OutputStream out = socket.getOutputStream();
        BufferedInputStream filein = new BufferedInputStream(new FileInputStream(file));

        System.out.println("Sending File...");
        while ((count = filein.read(buffer)) > 0) {
            out.write(buffer, 0, count);
            out.flush();
        }

        socket.close();
        filein.close();

        System.out.println("File successfully Sent!");
    }

    public void sendFileName(String fileName) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(fileName);
        dataOutputStream.flush(); // send the message
    }

    /**
     * command format jadoop <command[GET|PUT|LS|COUNT]> <filename>
     * @param args
     */
    public static void main(String[] args) {

        String[] testArgs = {"put", "files/cat.jpg"};
        args = testArgs;

        try {
            Client client = new Client("localhost", 1988);
            String action = args[0];

            client.sendAction(action.toUpperCase());

            if(action.equals("put")) {
                String filePath = args[1];
                client.putFile(filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
