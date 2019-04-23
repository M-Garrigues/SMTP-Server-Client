package src;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.*;

public class Client extends Observable {

    private boolean debug = true;

    private DatagramSocket ds;
    private int portToSend;
    private InetAddress ip;
    private int port;
    private int nextFileId = 0;

    private Socket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    private String host;
    private int hostPort;
    private String timeStamp;

    private boolean isAuthentificated = false;

    public Client(){}

    public Client(String ip, int port)
    {
        setIP(ip);
        this.port = port;
    }


    public static void main(String[] args) {

        System.out.println("SimpleEmail Start");

        String smtpHostServer = "localhost";
        String emailID = "user2@bestsmtpserver.com";

        Properties props = System.getProperties();

        props.put("mail.smtp.host", smtpHostServer);

        Session session = Session.getInstance(props, null);

        sendEmail(session, emailID,"SimpleEmail Testing Subject", "SimpleEmail Testing Body");
    }

    public static void sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("user1@bestsmtpserver.com", "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse("user1@bestsmtpserver.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTimeStamp(){
        return timeStamp;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void setIP(String ip)
    {
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static int portScanner() {
        int portLibre = 0;
        for (int port = 2000; port <= 4000; port++) {
            try {
                DatagramSocket server = new DatagramSocket(port);
                if(portLibre == 0)
                    portLibre = port;
                server.close();
            } catch (SocketException ex) {
            }
        }
        return portLibre;
    }

    public boolean connectToHost(String host, int port){

        try{
            socket = new Socket();

            socket.connect(new InetSocketAddress(host, port));

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String ret = readResponseLine();

            timeStamp = ret.substring(ret.lastIndexOf(" ")+1);
            System.out.println(timeStamp);
            return isConnected();

        }catch (IOException e){
            return false;
        }

    }

    public boolean isConnected(){
         return socket != null && socket.isConnected();
    }
    public boolean isAuthentificated() { return isAuthentificated;}

    public void disconnect(){

        try{
            socket.close();
        }catch(Exception E){
            System.out.println("Failed to disconnect.");
        }

        reader = null;
        writer = null;
        System.out.println("Disconnected from the host");
    }

    protected String readResponseLine() throws IOException{
        String response = reader.readLine();
        if (debug) {
            System.out.println("DEBUG [in] : " + response);
        }

        return response;
    }

    protected String sendCommand(String command) throws IOException {

        if (debug) {
            System.out.println("DEBUG [out]: " + command);
        }
        writer.write(command + "\n");
        writer.flush();
        return readResponseLine();
    }

    public boolean sendAPOP(String username, String pwd, String timestamp) throws IOException{

        System.out.println(timestamp+pwd);
        String password = encryptPassword(timestamp+pwd);

        String response =  sendCommand("APOP "+username+" "+password);


        if (response.startsWith("+OK")){
            if(debug){
                System.out.println("Authentification OK.");
            }
            isAuthentificated = true;
            return true;
        }else{
            if(debug){
                System.out.println("Authentification FAILED.");
            }
            isAuthentificated = false;
            return false;
        }
    }

    public void logout() throws IOException{
        sendCommand("QUIT");
        isAuthentificated = false;
    }

    public int getNumberOfNewMessages() throws IOException {
        String response = sendCommand("STAT");
        String[] values = response.split(" ");
        return Integer.parseInt(values[1]);
    }

    public void sendLIST(){

    }

    public void sendSTAT(){

    }

    public String retrieveMessage(int id){

        return new String();
    }


    protected void getMessage(int i) throws IOException {
        String response = sendCommand("RETR " + i);
        System.out.println(response);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        String headerName = null;
// process headers
        do {
            if (response.startsWith("\t")) {
                continue; //no process of multiline headers
            }
            int colonPosition = response.indexOf(":");
            headerName = response.substring(0, colonPosition);
            String headerValue;
            if (headerName.length() >= colonPosition) {
                headerValue = response.substring(colonPosition + 2);
            } else {
                headerValue = "papa";
            }
            List<String> headerValues = headers.get(headerName);
            if (headerValues == null) {
                headerValues = new ArrayList<String>();
                headers.put(headerName, headerValues);
            }
            headerValues.add(headerValue);
        }while ((response = readResponseLine()).length() != 0);
// process body
        StringBuilder bodyBuilder = new StringBuilder();
        while (!(response = readResponseLine()).equals(".")) {
            bodyBuilder.append(response + "\n");
        }
       // return new Message(headers, bodyBuilder.toString());
    }

    public List<Message> getMessages() throws IOException {
        int numOfMessages = getNumberOfNewMessages();
        List<Message> messageList = new ArrayList<Message>();
        for (int i = 1; i <= numOfMessages; i++) {
           // messageList.add(getMessage(i));
        }
        return messageList;
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
