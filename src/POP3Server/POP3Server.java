package POP3Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class POP3Server {
    private static final int PORT = 5442;

    public static void main(String[] args) throws IOException {
        List<POP3ServerThread> POP3ServerThreads = new ArrayList<>();
        ServerSocket serverSocket = new ServerSocket(PORT);

        while(true){
            POP3ServerThread POP3ServerThread = new POP3ServerThread(serverSocket.accept());
            POP3ServerThread.start();
            POP3ServerThreads.add(POP3ServerThread);
        }

    }
}