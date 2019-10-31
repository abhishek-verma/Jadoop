package com.abhishek.server;


import java.io.*;
import java.net.Socket;

public class WorkerRunnable implements Runnable {

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

        System.out.println("File successfully Received!");
    }

    private void getFileAction() throws IOException, ClassNotFoundException {
        InputStream socketIn = clientSocket.getInputStream();
        ObjectInputStream socketois = new ObjectInputStream(socketIn);

        String fileName = (String) socketois.readObject();

        OutputStream socketOut = clientSocket.getOutputStream();
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream filein = new BufferedInputStream(fis);

        byte[] buffer = new byte[1024];
        int count;


        System.out.println("Receiving File...");
        while ((count = filein.read(buffer)) > 0) {
            socketOut.write(buffer, 0, count);
            socketOut.flush();
        }

        filein.close();
        clientSocket.close();
        System.out.println("File successfully Received!");
    }
}