package SMTPServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SMTPServerThread extends Thread{
    //private static final List<String> users = new ArrayList<String>.

    private SMTPStateEnum serverState;
    private Socket socket;
    private InputStreamReader inputStreamReader;
    private PrintWriter out;
    private BufferedReader in;

    private String currentUser = "";
    private String currentMail;
    private ArrayList<String> currentMailRecipients;

    public SMTPServerThread(Socket socket) throws IOException {
        this.serverState = SMTPStateEnum.STOPPED;
        this.socket = socket;
    }

    @Override
    public void run(){
        serverState = SMTPStateEnum.READY;

        while(true){
            switch(serverState){
                case READY:
                    handleReadyState();
                    break;
                case WAITING_EHLO:
                    handleWaitingEhloState();
                    break;
                case WAITING_MAIL:
                    handleWaitingMailState();
                    break;
                case WAITING_RECIPIENT:
                    handleWaitingRecipientState();
                    break;
                case WAITING_DATA:
                    handleWaitingDataState();
                    break;
            }
        }

    }

    private void handleReadyState(){
        try {
            //Initialisation des canaux d'entrée et de sortie
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        //Passage dans l'état Waiting EHLO
        serverState = SMTPStateEnum.WAITING_EHLO;
        print("220 <bestsmtpserverever.com> Service Ready");
    }

    private void handleWaitingEhloState(){
        try {
            String input = in.readLine();
            String[] params = input.split(" ", 2);
            if(params[0].equals("EHLO")){
                serverState = SMTPStateEnum.WAITING_MAIL;
                print("250 OK");
            }
            else if(params[0].equals("QUIT")){
                serverState = SMTPStateEnum.READY;
                print("250 OK");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void handleWaitingMailState(){
        try{
            String input = in.readLine();
            String[] params = input.split(" ", 2);
            for (String param: params) {
                System.out.println(param);
            }
            if(params[0].equals("MAIL")){
                int firstDelimiterIndex = params[1].indexOf("FROM:<"), lastDelimiterIndex = params[1].lastIndexOf("@bestsmtpserver.com>");
                if(firstDelimiterIndex < 0 || lastDelimiterIndex < 0){
                    print("550 mailbox unavailable");
                }
                else{
                    String userName = params[1].substring(firstDelimiterIndex+6, lastDelimiterIndex);
                    if(!userName.equals("user1") && !userName.equals("user2") && !userName.equals("user3")){
                        print("550 no such user");
                    }
                    else{
                        currentUser = userName;
                        currentMail = "";
                        currentMailRecipients = new ArrayList<>();
                        print("250 OK");
                        serverState = SMTPStateEnum.WAITING_RECIPIENT;
                    }
                }
            }
            else if(params[0].equals("QUIT")){
                serverState = SMTPStateEnum.READY;
                print("250 OK");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void handleWaitingRecipientState(){
        try{
            String input = in.readLine();
            String[] params = input.split(" ", 2);
            if(params[0].equals("RCPT")){
                int firstDelimiterIndex = params[1].indexOf("TO:<"), lastDelimiterIndex = params[1].lastIndexOf("@bestsmtpserver.com>");
                if(firstDelimiterIndex < 0 || lastDelimiterIndex < 0){
                    print("550 mailbox unavailable");
                }
                else{
                    String userName = params[1].substring(firstDelimiterIndex+4, lastDelimiterIndex);
                    if(!userName.equals("user1") && !userName.equals("user2") && !userName.equals("user3")){
                        print("550 no such user");
                    }
                    else{
                        currentMailRecipients.add(userName);
                        print("250 OK");
                        serverState = SMTPStateEnum.WAITING_RECIPIENT;
                    }
                }
            }
            else if(params[0].equals("DATA")){
                if(currentMailRecipients.size() <= 0){
                    print("503 Bad sequence");
                }
                else{
                    print("354 Send message content; end with <CRLF>.<CRLF>");
                    serverState = SMTPStateEnum.WAITING_DATA;
                }
            }
            else if(params[0].equals("RSET")){
                currentUser = "";
                currentMail = "";
                currentMailRecipients = new ArrayList<>();
                print("250 OK");
                serverState = SMTPStateEnum.WAITING_MAIL;
            }
            else if(params[0].equals("QUIT")){
                serverState = SMTPStateEnum.READY;
                print("250 OK");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void handleWaitingDataState(){
        try{
            boolean isMessageComplete = false;
            while(!isMessageComplete){
                String input = in.readLine();
                isMessageComplete = input.equals(".");
                currentMail += input + "\n";

                if(isMessageComplete){
                    print(currentMail);

                    //Ecriture du message dans la boîte de chaque recipient
                    for(String recipient : currentMailRecipients){
                        //int currentMessageNumber = DB.STAT(recipient)[0];
                        //String fileName = "Utils.DB/" + recipient + "/" + recipient + "_" + (currentMessageNumber+1) + ".txt";
                        String fileName = "Utils.DB/" + recipient + "/" + recipient + "_" + (1+1) + ".txt";
                        File file = new File(fileName);
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file, true);
                        fos.write(currentMail.getBytes());
                        fos.flush();
                    }

                    print("250 OK");
                    serverState = SMTPStateEnum.WAITING_MAIL;
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }

    }

    public SMTPStateEnum getServerState(){
        return serverState;
    }

    private void print(String message){
        out.println(message);
        System.out.println(message);
    }
}