package SMTPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class SMTPServer {
    private static final int PORT = 5443;

    public static void main(String[] args) throws IOException {
        List<SMTPServerThread> SMTPServerThreads = new ArrayList<>();
        ServerSocket serverSocket = new ServerSocket(PORT);

        while(true){
            SMTPServerThread SMTPServerThread = new SMTPServerThread(serverSocket.accept());
            SMTPServerThread.start();
            SMTPServerThreads.add(SMTPServerThread);
        }

    }
}