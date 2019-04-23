package src;

import View.ErrorView;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

public class MailApp {


    Client client = new Client();
    List<Message> messages;
    int nbNewMessages = 0;
    Label error_label;
    TextField input_user;
    TextField input_ip;
    TextField input_port;
    PasswordField input_password;
    String errorMessage;
    public MailApp(Stage stage) {




        stage.setTitle("POP3 Client");
        stage.setWidth(600);
        stage.setHeight(180);




        Group root = new Group();
        String username = "username";
        String ip = "192.168.43.199";
        int port = 5442;

        BorderPane container = new BorderPane();
        VBox vBox = new VBox();

        // zone de changement de port et ip du serveur distant
        HBox hBox1 = new HBox();
        Label label_user = new Label(" Username : ");
        hBox1.getChildren().add(label_user);
        input_user = new TextField(username);
        hBox1.getChildren().add(input_user);

        HBox hBox2 = new HBox();
        Label label_password = new Label(" Password :  ");
        hBox2.getChildren().add(label_password);
        input_password = new PasswordField();
        hBox2.getChildren().add(input_password);

        HBox hBox3 = new HBox();
        Label label_ip = new Label(" SMTPServer IP :   ");
        hBox3.getChildren().add(label_ip);
        input_ip = new TextField(ip);
        hBox3.getChildren().add(input_ip);
        Label label_port = new Label("  SMTPServer Port : ");
        hBox3.getChildren().add(label_port);
        input_port = new TextField(Integer.toString(port));
        hBox3.getChildren().add(input_port);

        HBox hBox4 = new HBox();
        error_label = new Label("");
        error_label.setTextFill(Color.RED);
        Button button_connexion = new Button("Connexion");
        button_connexion.setOnAction(e -> {

            error_label.setText("  ");
            try
            {
                connect();
            }
            catch (IOException ex)
            {
                error_label.setText(" " +errorMessage);
            }
            if(client.isAuthentificated())
            {
                try
                {
                    refreshMessages();
                }
                catch (IOException ex)
                {
                    error_label.setText(" " + errorMessage);
                }

                if(nbNewMessages != 0)
                {
                    ScrollPane list_mail =new ScrollPane();
                    list_mail.setFitToHeight(true);
                    list_mail.setFitToWidth(false);
                    Label contentMail = new Label();
                    contentMail.setWrapText(true);
                    contentMail.setMaxWidth(540);
                    contentMail.setMinWidth(540);
                    list_mail.setPrefSize(300,350);
                    VBox vBoxMail = new VBox();

                    for(int i = 0; i< nbNewMessages;i++)
                    {
                        final int j = i;
                        StackPane s = new StackPane();
                        Rectangle r = new Rectangle(300,70);
                        r.setFill(Color.LIGHTGRAY);
                        r.setStroke(Color.BLACK);
                        s.setOnMouseClicked(new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent t) {

                                r.setFill(Color.WHITE);
                                contentMail.setText(messages.get(j).getBody());
                            }
                        });
                        Label l = new Label("From : " + messages.get(i).getHeaders().get("From").toString() +"\n Subject : "+ messages.get(i).getHeaders().get("Subject").toString());
                        s.getChildren().addAll(r,l);
                        vBoxMail.getChildren().add(s);
                    }

                    list_mail.setContent(vBoxMail);
                    BorderPane secondaryLayout = new BorderPane();
                    secondaryLayout.setLeft(list_mail);
                    secondaryLayout.setRight(contentMail);
                    Scene secondScene = new Scene(secondaryLayout, 900, 350);

                    // New window (Stage)
                    Stage newWindow = new Stage();
                    newWindow.setTitle("Display Email");
                    newWindow.setScene(secondScene);

                    newWindow.show();
                    newWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent we) {
                            try {
                                quit();
                            }
                            catch(IOException e)
                            {

                            }

                        }
                    });

                }
            }




        });

        hBox4.getChildren().add(button_connexion);
        hBox4.getChildren().add(error_label);
        vBox.getChildren().add(hBox1);
        vBox.getChildren().add(hBox2);
        vBox.getChildren().add(hBox3);
        vBox.getChildren().add(hBox4);



        container.setTop(vBox);






        root.getChildren().add(container);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        container.setPrefHeight(scene.getHeight());
        container.setPrefWidth(scene.getWidth());

        ErrorView errorView = new ErrorView(stage);
        client.addObserver(errorView);
    }




    public void connect() throws IOException {

        assert !client.isConnected() : "Client is already connnected";

        //checkChampsRemplis, erreur sinon

        String host = input_ip.getText();
        int port = Integer.parseInt(input_port.getText());
        String username = input_user.getText();
        String password = input_password.getText();

        if (client.connectToHost(host, port)){

            String timestamp = client.getTimeStamp();

            if( client.sendAPOP(username, password, timestamp)){

                //PASSER AU STAGE 2;
            }else{
                errorMessage = "Authentification failed.";
                throw new IOException();
            }
        }else{
            client.disconnect();

            errorMessage = "Can not reach server. Verify ip and port are correctly configured.";
            throw new IOException();
        }
    }

    public void quit()throws IOException{

        //Fonctions en plus
        disconnect();
    }

    public void disconnect()throws IOException{

        client.logout();
        client.disconnect();
    }

    public void refreshMessages() throws  IOException{

        try {
            nbNewMessages = client.getNumberOfNewMessages();
            //messages = client.getMessages();
        }
        catch(IOException ex)
        {
            errorMessage = "Cannot load messages";
        }
    }
}

