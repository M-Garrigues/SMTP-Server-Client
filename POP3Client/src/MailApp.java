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

import javax.mail.Session;
import java.io.IOException;
import java.util.*;

public class MailApp {


    Label error_label;
    TextField input_mail1;
    TextField input_mail2;
    TextField input_serv1;
    TextField input_serv2;
    TextField input_sub;
    TextArea input_body;

    String errorMessage;
    public MailApp(Stage stage) {




        stage.setTitle("POP3 Client");
        stage.setWidth(720);
        stage.setHeight(400);




        Group root = new Group();
        String serv1 = "localhost";
        String serv2 = "";
        String mailTo1 = "mailto@machin.bidule";
        String mailTo2 = "";
        String ip = "192.168.43.199";
        String subject = "Subject";
        String body = "\n";
        int port = 5442;

        BorderPane container = new BorderPane();
        VBox vBox = new VBox();

        // zone de changement de port et ip du serveur distant
        HBox hBox1 = new HBox();
        Label label_serv1 = new Label(" Receiver server : ");
        hBox1.getChildren().add(label_serv1);
        input_serv1 = new TextField(serv1);
        hBox1.getChildren().add(input_serv1);
        Label label_mail = new Label(" Receiver email : ");
        hBox1.getChildren().add(label_mail);
        input_mail1 = new TextField(mailTo1);
        hBox1.getChildren().add(input_mail1);
        HBox hBox1bis = new HBox();
        Label label_serv2 = new Label(" Receiver server : ");
        hBox1bis.getChildren().add(label_serv2);
        input_serv2 = new TextField(serv2);
        hBox1bis.getChildren().add(input_serv2);
        Label label_mail2 = new Label(" Receiver email : ");
        hBox1bis.getChildren().add(label_mail2);
        input_mail2 = new TextField(mailTo2);
        hBox1bis.getChildren().add(input_mail2);

        HBox hBox2 = new HBox();
        Label label_sub = new Label(" Subject ");
        hBox2.getChildren().add(label_sub);
        input_sub = new TextField(subject);
        hBox2.getChildren().add(input_sub);
        HBox hBox3 = new HBox();
        Label label_body = new Label("  Body : ");

        hBox3.getChildren().add(label_body);
        input_body = new TextArea(body);

        hBox3.getChildren().add(input_body);

        HBox hBox4 = new HBox();
        error_label = new Label("");
        error_label.setTextFill(Color.RED);
        Button button_connexion = new Button("Envoi");
        button_connexion.setOnAction(e -> {
        Map<String,List<String>> receivers = getMailadresses();
            for (String host : receivers.keySet())
            {
                for (String mail: receivers.get(host))
                {
                    Properties props = System.getProperties();

                    props.put("mail.smtp.host", host);
                    props.put("mail.smtp.auth", "false");
                    props.put("mail.debug", "false");


                    Session session = Session.getInstance(props, null);

                    Client.sendEmail(session, mail,input_sub.getText(), input_body.getText());
                }
            }
        });





        hBox4.getChildren().add(button_connexion);
        hBox4.getChildren().add(error_label);
        vBox.getChildren().add(hBox1);
        vBox.getChildren().add(hBox1bis);
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


    }
    public Map<String,List<String>> getMailadresses()
    {
        Map<String,List<String>> result = new HashMap<String, List<String>>();

        List<String> a = Arrays.asList(input_mail1.getText().split(";"));
        result.put(input_serv1.getText(),a);
        a =Arrays.asList(input_mail2.getText().split(";"));
        result.put(input_serv2.getText(),a);
        System.out.println(result.toString());
        return result;



    }
}

