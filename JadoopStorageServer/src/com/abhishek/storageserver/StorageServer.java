package com.abhishek.storageserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageServer extends Thread {

    public final static String TAG = StorageServer.class.getSimpleName() + " | ";

    private ServerSocket serverSock;
    protected ExecutorService threadPool =
            Executors.newFixedThreadPool(4);

    public StorageServer(int port) {
        try {
            serverSock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            System.out.println(TAG + "Waiting for a Client...");
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSock.accept();
                System.out.println(TAG + "Client Connected, Serving Client...");
                this.threadPool.execute(
                        new WorkerRunnable(clientSocket));
            } catch (IOException e) {
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
        }
    }

    public static void main(String[] args) {
        StorageServer fs = new StorageServer(2001);
        fs.start();
    }

}