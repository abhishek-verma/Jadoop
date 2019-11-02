package com.abhishek.server;


import java.io.*;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.util.List;

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

        } catch (NotActiveException e) {
           System.err.println("[ERROR] Operation Failed: " + e.getMessage());
        } catch (NoSuchFileException e) {
            System.err.println("[ERROR] Cannot fetch hosts from: " + e.getMessage());
        } catch (IOException  e) {
            //report exception somewhere.
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Action getAction() throws IOException, IllegalArgumentException {
        InputStream socketIn = clientSocket.getInputStream();
        DataInputStream socketdis = new DataInputStream(socketIn);
        String action = socketdis.readUTF();

        return Action.valueOf(action);
    }

    private void putFileAction() throws IOException {
        int ports = 2001;

        List<String> hosts = HostsHelper.fetchHosts();

        StorageConnections storageConnections = new StorageConnections(hosts, ports);

        storageConnections.sendAction("PUT");
        storageConnections.putFileAction(clientSocket);

        storageConnections.closeAll();

        sendSuccess();

        System.out.println(TAG + "File successfully Forwarded to Storage Server!");
    }

    private void getFileAction() throws IOException {
        int ports = 2001;

        List<String> hosts = HostsHelper.fetchHosts();

        StorageConnections storageConnections = new StorageConnections(hosts, ports);

        storageConnections.sendAction("GET");
        storageConnections.getFileAction(clientSocket);

        storageConnections.closeAll();

        System.out.println(TAG + "File successfully Forwarded to Client!");
    }

    public void sendSuccess() throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF("SUCCESS");
        dataOutputStream.flush(); // send the message
    }
}