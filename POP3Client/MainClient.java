import javafx.application.Application;
import javafx.stage.Stage;
import src.MailApp;

/**
 * Created by mathieu garrigues on 17/05/2017.
 */
public class MainClient extends Application {

    public void start(Stage primaryStage) throws Exception{
        new MailApp(primaryStage);

        /*Client client = new Client();
        client.connectToHost("localhost", 69);
        client.sendAPOP("name@myserver.com", "password");
        System.out.println("Number of new emails: " + client.getNumberOfNewMessages());
        List<Message> messages = client.getMessages();
        for (int index = 0; index < messages.size(); index++) {
            System.out.println("--- Message num. " + index + " ---");
            System.out.println(messages.get(index).getBody());
        }
        client.logout();
        client.disconnect();*/
    }

    public static void main(String[] args) {
        launch(args);
    }

}
