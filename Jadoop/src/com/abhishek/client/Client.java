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

    public void putFile(String filepath, String destName) throws IOException, InterruptedException {
        File file = new File(filepath);
        int count;
        byte[] buffer = new byte[1024];

        // Sending file name
        sendFileSize(file.length(), destName);
        sendFileName(destName);

        // Sending the file
        OutputStream out = socket.getOutputStream();
        BufferedInputStream filein = new BufferedInputStream(new FileInputStream(file));

        System.out.println(TAG + "Sending File...");
        while ((count = filein.read(buffer)) > 0) {
            out.write(buffer, 0, count);
            out.flush();
        }

        socket.shutdownOutput();

        filein.close();

        InputStream socketIn = socket.getInputStream();
        DataInputStream socketdis = new DataInputStream(socketIn);
        String result = socketdis.readUTF();

        if(!result.equals("SUCCESS"))
            throw new InterruptedException("Server error!");


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
        while((count=socketIn.read(buffer)) > 0){
            fileos.write(buffer, 0, count);
        }

        fileos.close();

        if(file.length() > 0)
            System.out.println(TAG + "File successfully Received! Size: " + file.length() + " bytes");
        else {
            file.delete();
            throw new FileNotFoundException("File not found on remote server");
        }
    }

    public void sendFileName(String fileName) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(fileName);
        dataOutputStream.flush(); // send the message
    }

    public void sendFileSize(long fileSize, String fileName) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeLong(fileSize + fileName.length());
        dataOutputStream.flush(); // send the message
    }

    private void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * command format jadoop <command[GET|PUT|LS|COUNT]> <file> <file>
     * @param args
     */
    public static void main(String[] args) {

//        String[] testArgs = {"put", "files/cat.jpg", "cat.jpg"};
//        String[] testArgs = {"get", "cat.jpg", "files/receivedCat.jpg"};
//        args = testArgs;
        Client client = null;
        try {
             client = new Client("localhost", 1988);
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
        } catch (IndexOutOfBoundsException e) {
            System.err.println("command format: \n[1] java -jar Jadoop.jar put <file_path> <dest_id> \n[2] java -jar get <file_id> <dest_path>" );
        } catch (IOException | InterruptedException e) {
            System.err.println("[ERROR] Cannot complete Operation due to: " + e.getMessage());
        } finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

}
