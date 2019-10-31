package com.abhishek.storageserver;


import java.io.*;
import java.net.Socket;

public class WorkerRunnable implements Runnable{

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

    private Action getAction() throws IOException, IllegalArgumentException {
        System.out.println(TAG + "Receiving Action...");
        InputStream socketIn = clientSocket.getInputStream();
        DataInputStream socketdis = new DataInputStream(socketIn);

        String action = socketdis.readUTF();

        System.out.println(TAG + "Received Action: " + action);

        return Action.valueOf(action);
    }

    private void putFileAction() throws IOException {
        InputStream socketIn = clientSocket.getInputStream();
        DataInputStream socketdis = new DataInputStream(socketIn);

        String fileName = socketdis.readUTF();

        FileOutputStream fileos = new FileOutputStream("filestorage/" + fileName);
        byte[] buffer = new byte[1024];
        int count;

        System.out.println(TAG + "Receiving File from Client...");
        while((count=socketIn.read(buffer)) > 0){
            fileos.write(buffer, 0, count);
        }

        fileos.close();
        clientSocket.close();
        System.out.println(TAG + "File successfully Received!");
    }

    private void getFileAction() throws IOException, ClassNotFoundException {
        InputStream socketIn = clientSocket.getInputStream();
        DataInputStream socketdis = new DataInputStream(socketIn);

        String fileName = socketdis.readUTF();

        BufferedInputStream filein = new BufferedInputStream(new FileInputStream("filestorage/" + fileName));
        OutputStream socketOut = clientSocket.getOutputStream();

        byte[] buffer = new byte[1024];
        int count;

        System.out.println(TAG + "Sending File to Client...");
        while ((count = filein.read(buffer)) > 0){
            socketOut.write(buffer, 0, count);
            socketOut.flush();
        }

        clientSocket.close();
        filein.close();

        System.out.println(TAG + "File successfully Sent!");
    }
}