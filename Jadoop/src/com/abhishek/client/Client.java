package com.abhishek.client;

import java.io.*;
import java.net.Socket;

public class Client {

    public final static String TAG = Client.class.getSimpleName() + " | ";

    private Socket socket;

    public Client(String host, int port) throws IOException {
        System.out.println(TAG + "Connecting to Server...");
        socket = new Socket(host, port);
    }

    public void sendAction(String action) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(action);
        dataOutputStream.flush(); // send the message
    }

    public void putFile(String filepath, String destName) throws IOException {
        File file = new File(filepath);
        int count;
        byte[] buffer = new byte[1024];

        // Sending file name
        sendFileName(destName);

        // Sending the file
        OutputStream out = socket.getOutputStream();
        BufferedInputStream filein = new BufferedInputStream(new FileInputStream(file));

        System.out.println(TAG + "Sending File...");
        while ((count = filein.read(buffer)) > 0) {
            out.write(buffer, 0, count);
            out.flush();
        }

        socket.close();
        filein.close();

        System.out.println(TAG + "File successfully Sent!");
    }

    public void getFile(String fileId, String destPath) throws IOException {
        File file = new File(destPath);

        // Sending file name
        sendFileName(fileId);

        // Receiving the file
        InputStream socketIn = socket.getInputStream();
        DataInputStream socketDis = new DataInputStream(socketIn);

        FileOutputStream fileos = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int count;

        System.out.println(TAG + "Receiving File...");
        while((count=socketIn.read(buffer)) >0){
            fileos.write(buffer, 0, count);
        }

        fileos.close();
        socket.close();

        System.out.println(TAG + "File successfully Received!");
    }

    public void sendFileName(String fileName) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(fileName);
        dataOutputStream.flush(); // send the message
    }

    /**
     * command format jadoop <command[GET|PUT|LS|COUNT]> <filename>
     * @param args
     */
    public static void main(String[] args) {

//        String[] testArgs = {"put", "files/cat.jpg", "cat.jpg"};
        String[] testArgs = {"get", "cat.jpg", "files/receivedCat.jpg"};
        args = testArgs;

        try {
            Client client = new Client("localhost", 1988);
            String action = args[0];

            client.sendAction(action.toUpperCase());

            if(action.equals("put")) {
                String filePath = args[1];
                String destName = args[2];
                client.putFile(filePath, destName);
            } else if(action.equals("get")) {
                String fileId = args[1];
                String destPath = args[2];
                client.getFile(fileId, destPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
