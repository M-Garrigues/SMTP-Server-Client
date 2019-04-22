package View;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Mathieu on 09/06/2018.
 */
public class ErrorView implements Observer {

    Label errorMessage;
    Stage stage;
    Popup popup;
    public ErrorView(Stage stage)
    {
        this.stage = stage;
        popup = new Popup();
        popup.setX(Screen.getPrimary().getBounds().getMaxX()/2-100);
        popup.setY(0);
        Rectangle rec = new Rectangle(0,0,200,50);
        rec.setFill(Color.RED);
        rec.setArcHeight(5d);
        rec.setArcWidth(7d);
        rec.setStroke(Color.LIGHTGRAY);
        rec.setStrokeWidth(3d);
        popup.getContent().addAll(rec);
        errorMessage = new Label("This is an error");
        errorMessage.setTextFill(Color.WHITE);
        errorMessage.setTextAlignment(TextAlignment.CENTER);
        errorMessage.setFont(new Font(15));
        popup.getContent().add(errorMessage);
        Button close = new Button("X");
        close.setLayoutY(0);
        close.setLayoutX(180);
        close.setOnAction(e -> {
            popup.hide();
        });
        popup.getContent().add(close);
    }
    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                String[] args = (String[]) arg;
                if (args[0] == "error")
                {
                    errorMessage.setText(args[1]);
                    popup.show(stage);
                }
            }
        });
    }
}
