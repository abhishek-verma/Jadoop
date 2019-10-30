package com.abhishek.storageserver;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            saveFile(clientSocket);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    private void saveFile(Socket clientSock) throws IOException {
        String receivedPath = "received.jpg";
        FileOutputStream fos = new FileOutputStream(receivedPath);
        byte[] buffer = new byte[1024];
        int count;

        InputStream in = clientSock.getInputStream();

        System.out.println("Receiving File...");
        while((count=in.read(buffer)) >0){
            fos.write(buffer, 0, count);
        }
        fos.close();
        clientSock.close();
        System.out.println("File successfully Received!");
    }
}