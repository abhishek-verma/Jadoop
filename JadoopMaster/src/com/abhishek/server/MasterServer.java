package com.abhishek.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MasterServer extends Thread {

    private ServerSocket serverSock;
    protected ExecutorService threadPool =
            Executors.newFixedThreadPool(4);

    public MasterServer(int port) {
        try {
            serverSock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            System.out.println("Waiting for a Client...");
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSock.accept();
                System.out.println("Client Connected, Serving Client...");
                this.threadPool.execute(new WorkerRunnable(clientSocket));
            } catch (IOException e) {
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
        }
    }

    public static void main(String[] args) {
        MasterServer fs = new MasterServer(1988);
        fs.start();
    }

}