package com.abhishek.storageserver;


import java.io.*;
import java.net.Socket;

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;

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
            putFileAction();
        } catch (IOException | ClassNotFoundException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    private Action getAction() throws IOException, ClassNotFoundException, IllegalArgumentException {
        InputStream socketIn = clientSocket.getInputStream();
        ObjectInputStream socketois = new ObjectInputStream(socketIn);

        String action = "";
        action = (String) socketois.readObject();

        return Action.valueOf(action);
    }

    private void putFileAction() throws IOException, ClassNotFoundException {
        InputStream socketIn = clientSocket.getInputStream();
        ObjectInputStream socketois = new ObjectInputStream(socketIn);

        String fileName = (String) socketois.readObject();

        FileOutputStream fileos = new FileOutputStream(fileName);
        byte[] buffer = new byte[1024];
        int count;

        System.out.println("Receiving File...");
        while((count=socketIn.read(buffer)) >0){
            fileos.write(buffer, 0, count);
        }

        fileos.close();
        clientSocket.close();
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
        while((count=filein.read(buffer)) > 0){
            socketOut.write(buffer, 0, count);
            socketOut.flush();
        }

        filein.close();
        clientSocket.close();
        System.out.println("File successfully Received!");
    }
}