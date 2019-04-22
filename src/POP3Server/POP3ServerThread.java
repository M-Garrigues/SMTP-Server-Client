package POP3Server;

import Utils.DB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Formatter;

public class POP3ServerThread extends Thread{
    //private static final List<String> users = new ArrayList<String>.

    private POP3StateEnum serverState;
    private Socket socket;
    private InputStreamReader inputStreamReader;
    private PrintWriter out;
    private BufferedReader in;

    private String currentWelcomeMessage;
    private String currentUser;
    private int passwordErrors = 0;

    public POP3ServerThread(Socket socket) throws IOException {
        this.serverState = POP3StateEnum.STOPPED;
        this.socket = socket;
    }

    @Override
    public void run(){
        serverState = POP3StateEnum.READY;

        while(true){
            switch(serverState){
                case READY:
                    handleReadyState();
                    break;
                case AUTHORIZATION:
                    handleAuthorizationState();
                    break;
                case WAITING_PASSWORD:
                    handleWaitingPasswordState();
                    break;
                case TRANSACTION:
                    handleTransactionState();
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

        //Passage dans l'état Authorization
        serverState = POP3StateEnum.AUTHORIZATION;
        this.setCurrentWelcomeMessage();
        print("+OK POP3 server ready " + currentWelcomeMessage);
    }

    private void handleAuthorizationState(){
        try {
            String input = in.readLine();
            if(!(input == null)){
                String[] params = input.split(" ", 2);
                if(params[0].equals("USER")){
                    if(!params[1].equals("user1") && !params[1].equals("user2") && !params[1].equals("user3")){
                        print("-ERR unknown user : " + params[1]);
                    }
                    else{
                        currentUser = params[1];
                        serverState = POP3StateEnum.WAITING_PASSWORD;

                        print("+OK");
                    }
                }
                else if(params[0].equals("APOP")){
                    System.out.println("Tentative de APOP");
                    String[] apopParams = params[1].split(" ", 2);
                    if(!apopParams[0].equals("user1") && !apopParams[0].equals("user2") && !apopParams[0].equals("user3")){
                        print("-ERR unknown user : " + apopParams[0]);
                    }
                    else{
                        String rightPwd = encryptPassword(currentWelcomeMessage+"1234");
                        if(!apopParams[1].equals(rightPwd)){
                            passwordErrors++;
                            System.out.println(rightPwd);

                            if(passwordErrors >= 3){
                                print("-ERR Invalid password. Too many errors, closing connection...");
                                socket.close();
                                serverState = POP3StateEnum.READY;
                            }
                            else{
                                print("-ERR Invalid password.");
                                serverState = POP3StateEnum.AUTHORIZATION;
                            }
                        }
                        else{
                            passwordErrors = 0;
                            currentUser = apopParams[0];
                            serverState = POP3StateEnum.TRANSACTION;
                            print("+OK");
                        }
                    }
                }
            }else{
                System.out.println("Client déconnecté.");
                try{
                    this.sleep(15000000);
                }catch (Exception e){

                }
            }

        } catch (IOException e) {

        }
    }

    private void handleWaitingPasswordState(){
        try {
            String input = in.readLine();
            String[] params = input.split(" ", 2);
            if(params[0].equals("PASS")){
                if(!params[1].equals("1234")){
                    passwordErrors++;

                    if(passwordErrors >= 3){
                        print("-ERR Invalid password. Too many errors, closing connection...");
                        socket.close();
                        serverState = POP3StateEnum.READY;
                    }
                    else{
                        print("-ERR Invalid password.");
                        serverState = POP3StateEnum.AUTHORIZATION;
                    }
                }
                else{
                    passwordErrors = 0;
                    currentUser = params[1];
                    serverState = POP3StateEnum.TRANSACTION;
                    print("+OK");
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void handleTransactionState(){
        try {
            String input = in.readLine();
            String[] params = input.split(" ", 2);
            if(params[0].equals("STAT")){
                int[] stat = DB.STAT(currentUser);
                print("+OK " + stat[0] + " " + stat[1]);
            }
            else if(params[0].equals("RETR")){
                String filePath = "DB/" + currentUser + "/" + currentUser + "_" + params[1] + ".txt";
                String message = DB.getMessage(filePath);
                if(message.equals("")){
                    print("-ERR " + params[1] + " not exists");
                }
                else{
                    print(/*"+OK " + message.length() + "\n" + */message);
                }
            }
            else if(params[0].equals("QUIT")){
                serverState = POP3StateEnum.READY;
                currentUser = null;
                print("+OK dewey POP3 server signing off");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void setCurrentWelcomeMessage(){
        long threadId = this.getId();
        long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        currentWelcomeMessage = "<" + threadId + "." + timestamp + "@bestpop3serverever.com>";
    }

    public POP3StateEnum getServerState(){
        return serverState;
    }

    private void print(String message){
        out.println(message);
        System.out.println(message);
    }

    private static String encryptPassword(String password)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}