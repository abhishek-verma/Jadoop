package com.abhishek.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StorageConnections {
    
    public final static String TAG = StorageConnections.class.getSimpleName() + " | ";

    public ArrayList<Socket> sockets;

    public StorageConnections(List<String> host, int port) throws NotActiveException {
        sockets = new ArrayList<>();
        System.out.println(TAG + "Connecting to Storage server node ...");

        try {
            for(int i=0; i<host.size(); i++) {
                sockets.add(new Socket(host.get(i), port));
            }
            System.out.println(TAG + "Connected to available nodes!");
        } catch (IOException e) {
            throw  new NotActiveException("Failed to Connect to some Storage nodes!!");
        }
    }

    public void sendAction(String action) throws IOException {
        System.out.println(TAG + "Sending Action: " + action);
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

        // getting file size
        DataInputStream socketdis = new DataInputStream(socketIn);
        long fileSize = socketdis.readLong();

        System.out.println(TAG + "Forwarding File from Client to storage ...");
        while ((count = socketIn.read(buffer)) > 0 && fileSize > 0) {
            for(Socket socket: sockets) {
                socket.getOutputStream().write(buffer, 0, count);
                socket.getOutputStream().flush();
            }
            fileSize -= count;
        }

        System.out.println(TAG + "Forwarding File from Client to storage complete");
    }

    public void getFileAction(Socket clientSocket) throws IOException {
        // Getting first available storage
        Socket socket = HostsHelper.getFirstAvailableStorage(sockets);

        if(socket == null)
            throw  new NotActiveException("No active Storage nodes");

        // getting file name
        InputStream clientIs = clientSocket.getInputStream();
        DataInputStream clientDis = new DataInputStream(clientIs);
        String fileName = clientDis.readUTF();

        // sending file name to storage
        OutputStream storageServerOs = sockets.get(0).getOutputStream();
        DataOutputStream storageServerDos = new DataOutputStream(storageServerOs);
        storageServerDos.writeUTF(fileName);
        storageServerDos.flush();

        // Getting first element because in this case there will be only one
        InputStream socketIn = sockets.get(0).getInputStream();

        byte[] buffer = new byte[1024];
        int count;

        System.out.println(TAG + "Forwarding file from Storage to client ...");
        OutputStream clientOs = clientSocket.getOutputStream();

        while((count = socketIn.read(buffer)) > 0) {
            clientOs.write(buffer, 0, count);
            clientOs.flush();
        }

        System.out.println(TAG + "Forwarding file from Storage to client complete");
    }

    public void closeAll() {
        for(Socket s : sockets) {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(TAG + "Closed all connections to server");
    }
}
