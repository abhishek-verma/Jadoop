package com.abhishek.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HostsHelper {

    public static List<String> fetchHosts() throws IOException {
        List<String> hosts = Files.readAllLines(Paths.get("hosts.txt"));
        return hosts;
    }

    public static Socket getFirstAvailableStorage(ArrayList<Socket> sockets) {

        for(Socket socket : sockets) {
            try {
                if(socket.getInetAddress().isReachable(3))
                    return socket;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
