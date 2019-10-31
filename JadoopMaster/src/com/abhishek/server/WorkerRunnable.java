package com.abhishek.server;


import java.io.*;
import java.net.Socket;

public class WorkerRunnable implements Runnable {

    public final static String TAG = WorkerRunnable.class.getSimpleName() + " | ";

    protected Socket clientSocket;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            switch (getAction()) {
                case PUT:
                    putFileAction();
                    break;
                case GET:
                    getFileAction();
                    break;
                case LS:
                    break;
                case COUNT:
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    private Action getAction() throws IOException, ClassNotFoundException, IllegalArgumentException {
        InputStream socketIn = clientSocket.getInputStream();
        DataInputStream socketdis = new DataInputStream(socketIn);
        String action = socketdis.readUTF();

        return Action.valueOf(action);
    }

    private void putFileAction() throws IOException {
        String[] hosts = { "localhost" };
        int[] ports = { 2001 };

        StorageConnections storageConnections = new StorageConnections(hosts, ports);

        storageConnections.sendAction("PUT");
        storageConnections.putFileAction(clientSocket);

        clientSocket.close();
        storageConnections.closeAll();

        System.out.println(TAG + "File successfully Forwarded to Storage Server!");
    }

    private void getFileAction() throws IOException, ClassNotFoundException {
        String[] hosts = { "localhost" };
        int[] ports = { 2001 };

        StorageConnections storageConnections = new StorageConnections(hosts, ports);

        storageConnections.sendAction("GET");
        storageConnections.getFileAction(clientSocket);

        clientSocket.close();
        storageConnections.closeAll();

        System.out.println(TAG + "File successfully Forwarded to Client!");
    }
}