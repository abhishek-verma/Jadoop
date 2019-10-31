package com.abhishek.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class StorageConnections {

    public ArrayList<Socket> sockets;

    public StorageConnections(String[] host, int[] port) throws IOException {
        sockets = new ArrayList<>();
        System.out.println("Connecting to Storage server node ...");

        for(int i=0; i<host.length; i++) {
            sockets.add(new Socket(host[i], port[i]));
        }
    }

    public void sendAction(String action) throws IOException {
        for(Socket socket : sockets) {
            OutputStream outputStream = socket.getOutputStream();
            // create a data output stream from the output stream so we can send data through it
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(action);
            dataOutputStream.flush(); // send the message
        }
    }

    public void putFileAction(Socket clientSocket) throws IOException {

        InputStream socketIn = clientSocket.getInputStream();

        byte[] buffer = new byte[1024];
        int count;

        System.out.println("Receiving File...");
        while ((count = socketIn.read(buffer)) > 0) {
            for(Socket socket: sockets) {
                socket.getOutputStream().write(buffer, 0, count);
                socket.getOutputStream().flush();
            }
        }
    }

    public void closeAll() {
        for(Socket s : sockets) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
